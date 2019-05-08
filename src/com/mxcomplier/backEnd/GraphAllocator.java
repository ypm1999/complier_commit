package com.mxcomplier.backEnd;

import com.mxcomplier.Error.IRError;
import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.MoveInstIR;
import com.mxcomplier.Ir.Operands.PhysicalRegisterIR;
import com.mxcomplier.Ir.Operands.StackSoltIR;
import com.mxcomplier.Ir.Operands.StaticDataIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.*;

import static com.mxcomplier.Ir.RegisterSet.*;

public class GraphAllocator{
    private static final int REGNUM = 14;

    private Graph graph, originGraph;
    private List<VirtualRegisterIR> spilledVregs;
    private LinkedList<VirtualRegisterIR> finishedStack;
    private HashSet<VirtualRegisterIR> simplifyTODOList, spillTODOList;
    private HashMap<VirtualRegisterIR, PhysicalRegisterIR> colorMap;

    private void init(){
        simplifyTODOList = new HashSet<>();
        spillTODOList = new HashSet<>();
        colorMap = new HashMap<>();
        spilledVregs = new ArrayList<>();
        finishedStack = new LinkedList<>();
        for (VirtualRegisterIR node:graph.getnodes()){
            if (graph.getDegree(node) < REGNUM)
                simplifyTODOList.add(node);
            else
                spillTODOList.add(node);
        }
    }

    private void doSimplify(){
        VirtualRegisterIR node = simplifyTODOList.iterator().next();
        simplifyTODOList.remove(node);
        HashSet<VirtualRegisterIR> neighbor = graph.getNeighbor(node);
        graph.removeNode(node);
        for (VirtualRegisterIR vreg : neighbor)
            if (graph.getDegree(vreg) < REGNUM && !simplifyTODOList.contains(vreg)){
                simplifyTODOList.add(vreg);
                spillTODOList.remove(vreg);
            }
        finishedStack.addFirst(node);
    }

    private void doSpill(){
        if (spillTODOList.isEmpty())
            return;
        VirtualRegisterIR spillCandidate = null;
        int maxDegree = -2;
        for (VirtualRegisterIR vreg:spillTODOList){
            if (vreg.getPhyReg() != null)
                continue;

            int curDeegree = graph.getDegree(vreg);
            if (curDeegree > maxDegree){
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
            if (graph.getDegree(vreg) < REGNUM && !simplifyTODOList.contains(vreg)){
                simplifyTODOList.add(vreg);
                spillTODOList.remove(vreg);
            }
        finishedStack.addFirst(spillCandidate);
    }

    private void assignColor(){
        colorMap = new HashMap<>();
        for (VirtualRegisterIR vreg : finishedStack){
            if (vreg.getPhyReg() != null) {
                colorMap.put(vreg, vreg.getPhyReg());
            }
        }
        for (VirtualRegisterIR vreg : finishedStack){
            if (vreg.getPhyReg() != null)
                continue;
            HashSet<PhysicalRegisterIR> colorCanUse = new HashSet<>(allocatePhyRegisterSet);
            for (VirtualRegisterIR neighbor : originGraph.getNeighbor(vreg))
                if (colorMap.containsKey(neighbor))
                    colorCanUse.remove(colorMap.get(neighbor));
            if (colorCanUse.isEmpty()){
                spilledVregs.add(vreg);
            }
            else{
                PhysicalRegisterIR preg = colorCanUse.iterator().next();
                colorCanUse.retainAll(calleeSaveRegisterSet);
                if (!colorCanUse.isEmpty())
                    preg = colorCanUse.iterator().next();
                colorMap.put(vreg, preg);
            }
        }
    }

    private IRBuilder irBuilder;
    private void rewriteFunc(FuncIR func){
//        for (VirtualRegisterIR vreg : spilledVregs){
//            System.err.println(vreg);
//            System.err.flush();
//        }
        for (VirtualRegisterIR vreg :spilledVregs)
            if (vreg.memory == null)
                vreg.memory = new StackSoltIR(vreg.lable + "_spillPlace");
        for (BasicBlockIR bb : func.getBBList()) {
            for(InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                List<VirtualRegisterIR> used = inst.getUsedVReg(), defined = inst.getDefinedVreg();
                used.retainAll(spilledVregs);
                defined.retainAll(spilledVregs);
                HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap = new HashMap<>();
                for (VirtualRegisterIR vreg: used) {
                    VirtualRegisterIR tmp = new VirtualRegisterIR("spill_reg");
                    renameMap.put(vreg, tmp);
                    inst.prepend(new MoveInstIR(tmp, vreg.memory));
                }
                InstIR curInst = inst;
                for (VirtualRegisterIR vreg: defined) {
                    VirtualRegisterIR tmp;
                    if (!renameMap.containsKey(vreg)) {
                        tmp = new VirtualRegisterIR("spill_reg");
                        renameMap.put(vreg, tmp);
                    }
                    else
                        tmp = renameMap.get(vreg);
                    inst.append(new MoveInstIR(vreg.memory,tmp));
                    inst = inst.next;
                }
                curInst.replaceVreg(renameMap);
            }
        }
    }

    private void runFunc(FuncIR func){
//        for (VirtualRegisterIR vreg : func.usedGlobalVar)
//            vreg.setPhyReg(null);
        while (true){
            originGraph = new LivenessAnalyzer().buildGraph(func);
            graph = new Graph(originGraph);
            init();
            do{
                if (!simplifyTODOList.isEmpty()) doSimplify();
                else doSpill();
            }while(!spillTODOList.isEmpty() || !simplifyTODOList.isEmpty());

            assignColor();

            if (spilledVregs.isEmpty()){
                //set phyReg
                for (VirtualRegisterIR vreg: originGraph.getnodes())
                    if (vreg.getPhyReg() == null) {
                        vreg.setPhyReg(colorMap.get(vreg));
                    }
                break;
            }
            else
                rewriteFunc(func);
//            new IRPrinter(irBuilder).visit(irBuilder.root);
//            System.out.flush();;
        }
    }

    public void run(IRBuilder ir){
        irBuilder = ir;
        int cnt = 0;
        for (StaticDataIR mem : ir.root.getStaticData()){
            mem.lable = "__Static" + cnt + "_" + mem.lable;
            cnt++;
        }
        for (FuncIR func: ir.root.getFuncs()){
            runFunc(func);
        }
    }
}
