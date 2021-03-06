package com.mxcomplier.backEnd;

import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DeadCodeEliminater extends IRScanner {

    IRBuilder ir;

    public DeadCodeEliminater(IRBuilder ir) {
        this.ir = ir;
    }

    public void run() {
        for (FuncIR funcIR : ir.root.getFuncs())
            runFunc(funcIR);
    }

    private boolean isRemoveAble(InstIR inst) {
        return inst instanceof BinaryInstIR || inst instanceof UnaryInstIR
                || inst instanceof MoveInstIR || inst instanceof LeaInstIR;
    }

    private void runFunc(FuncIR func) {
        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer();
        livenessAnalyzer.IRlevel = true;
        livenessAnalyzer.buildLiveOut(func);
        HashMap<BasicBlockIR, HashSet<VirtualRegisterIR>> liveOut = livenessAnalyzer.getLiveOut();

        for (BasicBlockIR bb : func.getBBList()) {
            HashSet<VirtualRegisterIR> liveNow = liveOut.get(bb);
            for (InstIR inst = bb.getTail().prev; inst != bb.getHead(); inst = inst.prev) {
                List<VirtualRegisterIR> used = inst.getUsedVReg(), defined = inst.getDefinedVreg();
                if (inst instanceof CallInstIR) {
                    used = ((CallInstIR) inst).getIRUsedVReg();
                    defined = ((CallInstIR) inst).getIRDefinedVreg();
                }
                boolean dead = true;
                if (defined.isEmpty())
                    dead = false;
                else {
                    for (VirtualRegisterIR vreg : defined)
                        if (liveNow.contains(vreg) || vreg == func.getReturnValue() || vreg.getMemory() != null) {
                            dead = false;
                            break;
                        }
                }
                if (isRemoveAble(inst) && dead) {
                    inst = inst.next;
                    inst.prev.remove();
                } else {
                    liveNow.removeAll(defined);
                    liveNow.addAll(used);
                }
            }
        }

        //merge move & binary
        for (BasicBlockIR bb : func.getBBList()) {
            HashSet<VirtualRegisterIR> liveNow = liveOut.get(bb);
            for (InstIR inst = bb.getTail().prev; inst != bb.getHead(); inst = inst.prev) {
                List<VirtualRegisterIR> used = inst.getUsedVReg(), defined = inst.getDefinedVreg();
                if (inst instanceof CallInstIR) {
                    used = ((CallInstIR) inst).getIRUsedVReg();
                    defined = ((CallInstIR) inst).getIRDefinedVreg();
                }
                boolean remove = false;
                if (inst instanceof MoveInstIR && inst.next instanceof BinaryInstIR) {
                    MoveInstIR move = (MoveInstIR) inst;
                    BinaryInstIR binary = (BinaryInstIR) inst.next;
                    if (binary.getSrc() == move.getDest()
                            && binary.getDest() instanceof VirtualRegisterIR
                            && move.getDest() instanceof VirtualRegisterIR
                            && !liveNow.contains(move.getDest()))
                        switch (binary.getOp()) {
                            case ADD:
                            case SUB:
                            case AND:
                            case OR:
                            case XOR:
                                binary.setSrc(move.getSrc());
                                remove = true;
                                break;
                        }
                }
                if (remove) {
                    inst = inst.next;
                    inst.prev.remove();
                } else {
                    liveNow.removeAll(defined);
                    liveNow.addAll(used);
                }
                liveNow.removeAll(defined);
                liveNow.addAll(used);
            }
        }

    }
}
