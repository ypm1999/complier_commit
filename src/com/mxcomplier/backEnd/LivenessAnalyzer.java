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

public class LivenessAnalyzer {

    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> liveOut = new HashMap<>();
    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> usedVregs = new HashMap<>();
    private HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> definedVregs = new HashMap<>();
    public boolean IRlevel;

    LivenessAnalyzer() {
        IRlevel = false;
    }

    HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> getLiveOut() {
        return liveOut;
    }

    void buildLiveOut(FuncIR func) {
        liveOut.clear();
        usedVregs.clear();
        definedVregs.clear();
        //get usedVregs & definedVregs
        for (BasicBlockIR bb : func.getBBList()) {
            HashSet<VirtualRegisterIR> bbUsed = new HashSet<>();
            HashSet<VirtualRegisterIR> bbDefined = new HashSet<>();
            liveOut.put(bb, new HashSet<>());
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
        //get liveOut
        boolean changed = true;
        func.initOrderBBList();
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

    Graph buildGraph(FuncIR func, List<Pair<VirtualRegisterIR, VirtualRegisterIR>> moveList) {
        Graph graph = new Graph();
        buildLiveOut(func);

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
