package com.mxcomplier.backEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.CallInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.MoveInstIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import org.antlr.v4.runtime.misc.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class LivenessAnalyzer {

    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> liveOut = new HashMap<>();
    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> liveIn = new HashMap<>();
    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> usedVregs = new HashMap<>();
    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> definedVregs = new HashMap<>();
    public boolean IRlevel;

    LivenessAnalyzer() {
        IRlevel = false;
    }

    HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> getLiveOut() {
        return liveOut;
    }

    public HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> getUsedVregs() {
        return usedVregs;
    }

    public HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> getDefinedVregs() {
        return definedVregs;
    }

    //get usedVregs & definedVregs
    public void initUseAndDef(FuncIR func){
        usedVregs.clear();
        definedVregs.clear();
        for (BasicBlockIR bb : func.getBBList()) {
            HashSet<VirtualRegisterIR> bbUsed = new HashSet<>();
            HashSet<VirtualRegisterIR> bbDefined = new HashSet<>();
            usedVregs.put(bb, bbUsed);
            definedVregs.put(bb, bbDefined);
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                List<VirtualRegisterIR> used = inst.getUsedVReg(), defined = inst.getDefinedVreg();
                if (IRlevel) {
                    if (inst instanceof CallInstIR) {
                        used = ((CallInstIR) inst).getIRUsedVReg();
                        defined = ((CallInstIR) inst).getIRDefinedVreg();
                    }
                }
                for (VirtualRegisterIR vreg : used)
                    if (!bbDefined.contains(vreg))
                        bbUsed.add(vreg);
                bbDefined.addAll(defined);
            }
        }
    }

    void buildLiveIn(FuncIR func) {
        initUseAndDef(func);
        liveIn.clear();
        for (BasicBlockIR bb : func.getBBList())
            liveIn.put(bb, new HashSet<>());
        //get liveIn
        boolean changed = true;
        func.initReverseOrderBBList();
        while (changed) {
            changed = false;
            for (BasicBlockIR bb : func.getOrderedBBList()) {
                int oldSize = liveIn.get(bb).size();
                HashSet<VirtualRegisterIR> curLiveIn = new HashSet<>();
                for (BasicBlockIR prevBB : bb.fronters) {
                    HashSet<VirtualRegisterIR> tempVregs = new HashSet<>(liveIn.get(prevBB));
                    tempVregs.removeAll(definedVregs.get(prevBB));
                    tempVregs.addAll(usedVregs.get(prevBB));
                    curLiveIn.addAll(tempVregs);
                }
                liveIn.remove(bb);
                liveIn.put(bb, curLiveIn);
                changed = changed || oldSize != curLiveIn.size();
            }
        }
    }

    void buildLiveOut(FuncIR func) {
        initUseAndDef(func);
        liveOut.clear();
        for (BasicBlockIR bb : func.getBBList())
            liveOut.put(bb, new HashSet<>());
        //get liveOut
        boolean changed = true;
        func.initReverseOrderBBList();
        while (changed) {
            changed = false;
            for (BasicBlockIR bb : func.getReversedOrderedBBList()) {
                int oldSize = liveOut.get(bb).size();
                HashSet<VirtualRegisterIR> curLiveOut = new HashSet<>();
                for (BasicBlockIR nextBB : bb.successors) {
                    HashSet<VirtualRegisterIR> tempVregs = new HashSet<>(liveOut.get(nextBB));

                    tempVregs.removeAll(definedVregs.get(nextBB));
                    tempVregs.addAll(usedVregs.get(nextBB));
                    curLiveOut.addAll(tempVregs);
                }
                liveOut.remove(bb);
                liveOut.put(bb, curLiveOut);
                changed = changed || oldSize != curLiveOut.size();
            }
        }
    }

    void liveOutRename(HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap){
        for (Map.Entry<BasicBlockIR, HashSet<VirtualRegisterIR>> entry: liveOut.entrySet()){
            HashSet<VirtualRegisterIR> livenow = entry.getValue();
            for (Map.Entry<VirtualRegisterIR, VirtualRegisterIR> rename: renameMap.entrySet()){
                if (livenow.contains(rename.getKey())){
                    livenow.remove(rename.getKey());
                    livenow.add(rename.getValue());
                }
            }
        }
    }

    Graph buildGraph(FuncIR func, List<Pair<VirtualRegisterIR, VirtualRegisterIR>> moveList) {
        buildLiveOut(func);
        return rebuildGraph(func, moveList);
    }

    Graph rebuildGraph(FuncIR func, List<Pair<VirtualRegisterIR, VirtualRegisterIR>> moveList) {
        Graph graph = new Graph();

        for (BasicBlockIR bb : func.getBBList()) {
            for (VirtualRegisterIR vreg : usedVregs.get(bb))
                graph.addNode(vreg);
            for (VirtualRegisterIR vreg : definedVregs.get(bb))
                graph.addNode(vreg);
        }

        for (BasicBlockIR bb : func.getBBList()) {
            HashSet<VirtualRegisterIR> liveNow = liveOut.get(bb);
            for (InstIR inst = bb.getTail().prev; inst != bb.getHead(); inst = inst.prev) {
                if (inst instanceof MoveInstIR && moveList != null) {
                    OperandIR dest = ((MoveInstIR) inst).getDest();
                    OperandIR src = ((MoveInstIR) inst).getSrc();
                    if (dest instanceof VirtualRegisterIR && src instanceof VirtualRegisterIR) {
                        moveList.add(new Pair<>((VirtualRegisterIR) dest, (VirtualRegisterIR) src));
                    }
                }
                List<VirtualRegisterIR> used = inst.getUsedVReg(), defined = inst.getDefinedVreg();
                graph.addEdges(new HashSet<>(defined), liveNow);
                liveNow.removeAll(defined);
                liveNow.addAll(used);
            }
        }
        return graph;
    }


}