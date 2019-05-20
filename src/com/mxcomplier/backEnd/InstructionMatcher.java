package com.mxcomplier.backEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.MoveInstIR;
import com.mxcomplier.Ir.Operands.MemoryIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.PhysicalRegisterIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Ir.ProgramIR;

public class InstructionMatcher extends IRScanner {


    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs()) {
            func.accept(this);
        }
    }

    @Override
    public void visit(FuncIR node) {
        for (BasicBlockIR bb : node.getBBList())
            bb.accept(this);
    }

    @Override
    public void visit(BasicBlockIR node) {
        for (InstIR inst = node.getHead().next; inst != node.getTail(); inst = inst.next) {
            if (inst instanceof MoveInstIR) {
                OperandIR dest = getPhyValue(((MoveInstIR) inst).getDest());
                OperandIR src = getPhyValue(((MoveInstIR) inst).getSrc());
                if (dest == src) {
                    inst = inst.prev;
                    inst.next.remove();
                } else if (inst.next instanceof MoveInstIR) {
                    OperandIR destNext = getPhyValue(((MoveInstIR) inst.next).getDest());
                    OperandIR srcNext = getPhyValue(((MoveInstIR) inst.next).getSrc());
                    if (dest == srcNext && src == destNext) {
                        inst.next.remove();
                        inst = inst.prev;
                    }
                    if (dest == destNext && dest instanceof PhysicalRegisterIR) {
                        if (((MoveInstIR) inst.next).getSrc() instanceof MemoryIR) {
                            MemoryIR temp = (MemoryIR) ((MoveInstIR) inst.next).getSrc();
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

        for (InstIR inst = node.getHead().next; inst != node.getTail(); inst = inst.next) {
            if (inst instanceof MoveInstIR && inst.next instanceof MoveInstIR) {
                MoveInstIR move = (MoveInstIR) inst;
                MoveInstIR moveNext = (MoveInstIR) inst.next;
                if (move.getSrc() instanceof MemoryIR) {
                    MemoryIR src = (MemoryIR) move.getSrc();
                    if (moveNext.getSrc() instanceof MemoryIR && src.phyEquals((MemoryIR) moveNext.getSrc())
                            && move.getDest() instanceof  VirtualRegisterIR && !src.getVreg().contains((VirtualRegisterIR) move.getDest())) {
                        moveNext.setSrc(move.getDest());
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
}