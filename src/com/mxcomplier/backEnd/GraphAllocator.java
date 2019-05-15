package com.mxcomplier.backEnd;

import com.mxcomplier.Error.IRError;
import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.MoveInstIR;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Ir.RegisterSet;
import org.antlr.v4.runtime.misc.Pair;

import java.util.*;

import static com.mxcomplier.Ir.RegisterSet.*;
import static java.lang.Math.min;

public class GraphAllocator {
    private static final int REGNUM = 14;

    private FuncIR curFunc = null;
    private Graph graph, originGraph;
    private List<VirtualRegisterIR> spilledVregs;
    private LinkedList<VirtualRegisterIR> finishedStack;
    private HashSet<VirtualRegisterIR> simplifyTODOList, spillTODOList;
    private HashMap<VirtualRegisterIR, PhysicalRegisterIR> colorMap;
    private LinkedList<PhysicalRegisterIR> allPhyreg;

    private VirtualRegisterIR getAlias(VirtualRegisterIR x) {
        if (x.alais == x)
            return x;
        return x.alais = getAlias(x.alais);
    }

    private boolean conservative(VirtualRegisterIR u, VirtualRegisterIR v) {
        HashSet<VirtualRegisterIR> nodes = new HashSet<>(graph.getNeighbor(u));
        nodes.addAll(graph.getNeighbor(v));
        int cnt = 0;
        for (VirtualRegisterIR node : nodes)
            if (graph.getDegree(node) >= REGNUM)
                cnt++;
        return cnt < REGNUM;
    }

    private void doMerge(FuncIR func) {
        List<Pair<VirtualRegisterIR, VirtualRegisterIR>> moveList = new ArrayList<>();
        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer();
        livenessAnalyzer.buildLiveOut(func);

        boolean changed = true;
        while(changed) {
            changed = false;
            moveList.clear();
            graph = livenessAnalyzer.buildGraph(func, moveList);
            for (VirtualRegisterIR node : graph.getnodes())
                node.alais = node;
            HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap = new HashMap<>();
            for (Pair<VirtualRegisterIR, VirtualRegisterIR> pair : moveList) {
                VirtualRegisterIR u = getAlias(pair.a), v = getAlias(pair.b);
                if (u == v)
                    continue;
                if (v.getPhyReg() != null) {
                    if (u.getPhyReg() != null)
                        continue;
                    else {
                        VirtualRegisterIR tmp = u;
                        u = v;
                        v = tmp;
                    }
                }
                if (!graph.getNeighbor(u).contains(v) && conservative(u, v)) {
//                    System.err.println("merge " + u + " <-" + v);
                    v.alais = u;
                    renameMap.put(v, u);
                    HashSet<VirtualRegisterIR> tmp = new HashSet<>(graph.getNeighbor(v));
                    for (VirtualRegisterIR node : tmp) {
                        graph.removeEdge(v, node);
                        graph.addEdge(u, node);
                    }
                    graph.removeNode(v);
                    changed = true;
                }
            }

            for (BasicBlockIR bb : func.getBBList()) {
                for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                    inst.replaceVreg(renameMap);
                    if (inst instanceof MoveInstIR && ((MoveInstIR) inst).src == ((MoveInstIR) inst).dest){
                        inst = inst.prev;
                        inst.next.remove();
                    }
                }
            }

//            livenessAnalyzer.liveOutRename(renameMap);
        }
    }

    private void init(FuncIR func) {
        simplifyTODOList = new HashSet<>();
        spillTODOList = new HashSet<>();
        colorMap = new HashMap<>();
        spilledVregs = new ArrayList<>();
        finishedStack = new LinkedList<>();
        allPhyreg = new LinkedList<>();
        for (int i = 0; i < min(6, func.getParameters().size()); i++)
            allPhyreg.addLast(paratReg[i].getPhyReg());
        for (PhysicalRegisterIR preg : callerSaveRegisterSet)
            if (!allPhyreg.contains(preg))
                allPhyreg.addLast(preg);
        for (PhysicalRegisterIR preg : allocatePhyRegisterSet)
            if (!allPhyreg.contains(preg))
                allPhyreg.addLast(preg);

        doMerge(func);

        originGraph = new LivenessAnalyzer().buildGraph(func, null);
        graph = new Graph(originGraph);


        for (VirtualRegisterIR node : graph.getnodes()) {
            if (graph.getDegree(node) < REGNUM)
                simplifyTODOList.add(node);
            else
                spillTODOList.add(node);
        }
    }

    private void doSimplify() {
        VirtualRegisterIR node = simplifyTODOList.iterator().next();
        simplifyTODOList.remove(node);
        HashSet<VirtualRegisterIR> neighbor = graph.getNeighbor(node);
        graph.removeNode(node);
        for (VirtualRegisterIR vreg : neighbor)
            if (graph.getDegree(vreg) < REGNUM && !simplifyTODOList.contains(vreg)) {
                simplifyTODOList.add(vreg);
                spillTODOList.remove(vreg);
            }
        finishedStack.addFirst(node);
    }

    private void doSpill() {
        if (spillTODOList.isEmpty())
            return;
        VirtualRegisterIR spillCandidate = null;
        int maxDegree = -2;
        for (VirtualRegisterIR vreg : spillTODOList) {
            if (vreg.getPhyReg() != null)
                continue;

            int curDeegree = graph.getDegree(vreg);
            if (curDeegree > maxDegree) {
                maxDegree = curDeegree;
                spillCandidate = vreg;
            }
        }
        if (spillCandidate == null)
            throw new IRError("can't spill");

        spillTODOList.remove(spillCandidate);
        HashSet<VirtualRegisterIR> neighbor = graph.getNeighbor(spillCandidate);
        graph.removeNode(spillCandidate);
        for (VirtualRegisterIR vreg : neighbor)
            if (graph.getDegree(vreg) < REGNUM && !simplifyTODOList.contains(vreg)) {
                simplifyTODOList.add(vreg);
                spillTODOList.remove(vreg);
            }
        finishedStack.addFirst(spillCandidate);
    }

    private void assignColor() {
        colorMap = new HashMap<>();
        for (VirtualRegisterIR vreg : finishedStack) {
            if (vreg.getPhyReg() != null) {
                colorMap.put(vreg, vreg.getPhyReg());
            }
        }
        for (VirtualRegisterIR vreg : finishedStack) {
            if (vreg.getPhyReg() != null)
                continue;
            List<PhysicalRegisterIR> colorCanUse = new LinkedList<>(allPhyreg);
            for (VirtualRegisterIR neighbor : originGraph.getNeighbor(vreg))
                if (colorMap.containsKey(neighbor))
                    colorCanUse.remove(colorMap.get(neighbor));
            if (colorCanUse.isEmpty()) {
                spilledVregs.add(vreg);
            } else {
                PhysicalRegisterIR preg = null;
//                for (int i = 0; i < min(6, curFunc.getParameters().size()); i++)
//                    if (colorCanUse.contains(paratReg[i].getPhyReg())) {
//                        preg = paratReg[i].getPhyReg();
//                        break;
//                    }
//                if (preg == null) {
                    preg = colorCanUse.iterator().next();
                    colorCanUse.retainAll(callerSaveRegisterSet);
                    if (!colorCanUse.isEmpty())
                        preg = colorCanUse.iterator().next();
//                }
                colorMap.put(vreg, preg);
            }
        }
    }

    private void rebuildSpilledList(){
        if (spilledVregs.size() < 3)
            return;
        int n = spilledVregs.size() - spilledVregs.size() / 3;
        while(--n > 0)
            spilledVregs.remove(0);
    }

    private IRBuilder irBuilder;

    private void rewriteFunc(FuncIR func) {
        rebuildSpilledList();
        for (VirtualRegisterIR vreg : spilledVregs)
            if (vreg.memory == null)
                vreg.memory = new StackSoltIR(vreg.lable + "_spillPlace");
        for (BasicBlockIR bb : func.getBBList()) {
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                List<VirtualRegisterIR> used = inst.getUsedVReg(), defined = inst.getDefinedVreg();
                used.retainAll(spilledVregs);
                defined.retainAll(spilledVregs);
                HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap = new HashMap<>();
                for (VirtualRegisterIR vreg : used) {
                    VirtualRegisterIR tmp = new VirtualRegisterIR("spill_reg");
                    renameMap.put(vreg, tmp);
                    inst.prepend(new MoveInstIR(tmp, vreg.memory));
                }
                InstIR curInst = inst;
                for (VirtualRegisterIR vreg : defined) {
                    VirtualRegisterIR tmp;
                    if (!renameMap.containsKey(vreg)) {
                        tmp = new VirtualRegisterIR("spill_reg");
                        renameMap.put(vreg, tmp);
                    } else
                        tmp = renameMap.get(vreg);
                    inst.append(new MoveInstIR(vreg.memory, tmp));
                    inst = inst.next;
                }
                curInst.replaceVreg(renameMap);
            }
        }
    }

    private void runFunc(FuncIR func) {
        func.initReverseOrderBBList();
        while (true) {
            init(func);
            do {
                if (!simplifyTODOList.isEmpty()) doSimplify();
                else doSpill();
            } while (!spillTODOList.isEmpty() || !simplifyTODOList.isEmpty());

            assignColor();

            if (spilledVregs.isEmpty()) {
                //set phyReg
                for (VirtualRegisterIR vreg : originGraph.getnodes())
                    if (vreg.getPhyReg() == null) {
                        vreg.setPhyReg(colorMap.get(vreg));
                    }
                break;
            } else
                rewriteFunc(func);
        }

        for (BasicBlockIR bb : func.getBBList()) {
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                if (inst instanceof MoveInstIR) {
                    OperandIR dest = getPhyValue(((MoveInstIR) inst).dest);
                    OperandIR src = getPhyValue(((MoveInstIR) inst).src);
                    if (dest == src) {
                        inst = inst.prev;
                        inst.next.remove();
                    } else if (inst.next instanceof MoveInstIR) {
                        OperandIR destNext = getPhyValue(((MoveInstIR) inst.next).dest);
                        OperandIR srcNext = getPhyValue(((MoveInstIR) inst.next).src);
                        if (dest == srcNext && src == destNext) {
                            inst.next.remove();
                            inst = inst.prev;
                        }
                        if (dest == destNext && dest instanceof PhysicalRegisterIR) {
                            if (((MoveInstIR) inst.next).src instanceof MemoryIR) {
                                MemoryIR temp = (MemoryIR) ((MoveInstIR) inst.next).src;
                                if (temp.getOffset() != null && temp.getOffset().getPhyReg() == dest)
                                    continue;
                                if (temp.getBase() != null && temp.getBase().getPhyReg() == dest)
                                    continue;
                            }
                            if (dest == srcNext)
                                continue;
                            inst = inst.prev;
                            inst.next.remove();
                        }
                    }
                }
            }
        }

    }

    private OperandIR getPhyValue(OperandIR oper) {
        if (oper instanceof VirtualRegisterIR)
            return ((VirtualRegisterIR) oper).getPhyReg();
        else
            return oper;
    }

    public void run(IRBuilder ir) {
        irBuilder = ir;
        int cnt = 0;
        for (StaticDataIR mem : ir.root.getStaticData()) {
            mem.lable = "__Static" + cnt + "_" + mem.lable;
            cnt++;
        }

        for (FuncIR func : ir.root.getFuncs()) {
            curFunc = func;
            runFunc(func);
            curFunc = null;
        }
    }
}
