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
            if (inst instanceof MoveInstIR && inst.next instanceof MoveInstIR) {
                MoveInstIR move = (MoveInstIR) inst;
                MoveInstIR moveNext = (MoveInstIR) inst.next;
                if (move.src instanceof MemoryIR){
                    MemoryIR src = (MemoryIR) move.src;
                    if (moveNext.src instanceof MemoryIR && src.phyEquals((MemoryIR) moveNext.src)){
                            moveNext.src = move.dest;
                    }
                }
            }
        }
    }
}