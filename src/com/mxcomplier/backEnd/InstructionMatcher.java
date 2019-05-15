package com.mxcomplier.backEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.BinaryInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.MoveInstIR;
import com.mxcomplier.Ir.Operands.MemoryIR;
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
        for (InstIR inst = node.getHead().next; inst != node.getTail(); inst = inst.next){
            if (inst instanceof MoveInstIR) {
                MoveInstIR move = (MoveInstIR) inst;
                if (inst.next instanceof BinaryInstIR) {
                    BinaryInstIR binary = (BinaryInstIR) inst.next;
                    if (move.dest instanceof VirtualRegisterIR
                            && binary.dest instanceof VirtualRegisterIR
                            && binary.src instanceof VirtualRegisterIR
                            && ((VirtualRegisterIR) binary.src).getPhyReg() == ((VirtualRegisterIR) move.dest).getPhyReg())
                        switch (binary.getOp()) {
                            case ADD:
                            case SUB:
                            case AND:
                            case OR:
                            case XOR:
                                inst = move.prev;
                                binary.src = move.src;
                                move.remove();
                                break;
                        }
                }
                else if (inst.next instanceof MoveInstIR && move.src instanceof MemoryIR){
                    MoveInstIR moveNext = (MoveInstIR) inst.next;
                    MemoryIR src = (MemoryIR) move.src;
                    if (moveNext.src instanceof MemoryIR && src.phyEquals((MemoryIR) moveNext.src)){
                            moveNext.src = move.dest;
                    }
                }
            }
        }
    }
}