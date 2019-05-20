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

class LivenessAnalyzer {

    boolean IRlevel;
    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> liveOut = new HashMap<>();
    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> usedVregs = new HashMap<>();
    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> definedVregs = new HashMap<>();

    LivenessAnalyzer() {
        IRlevel = false;
    }

    HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> getLiveOut() {
        return liveOut;
    }

    HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> getUsedVregs() {
        return usedVregs;
    }

    HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> getDefinedVregs() {
        return definedVregs;
    }

    //get usedVregs & definedVregs
    private void initUseAndDef(FuncIR func) {
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
                for (BasicBlockIR nextBB : bb.getSuccessors()) {
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


    Graph buildGraph(FuncIR func, List<Pair<VirtualRegisterIR, VirtualRegisterIR>> moveList) {
        buildLiveOut(func);
        return rebuildGraph(func, moveList);
    }

    private Graph rebuildGraph(FuncIR func, List<Pair<VirtualRegisterIR, VirtualRegisterIR>> moveList) {
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