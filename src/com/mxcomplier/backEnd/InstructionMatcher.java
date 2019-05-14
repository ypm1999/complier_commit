package com.mxcomplier.backEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.BinaryInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.JumpInstIR;
import com.mxcomplier.Ir.Instructions.MoveInstIR;
import com.mxcomplier.Ir.ProgramIR;

import java.util.List;

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
            node.accept(this);
    }

    @Override
    public void visit(BasicBlockIR node) {
        for (InstIR inst = node.getHead().next; inst != node.getTail(); inst = inst.next){
            if (inst instanceof MoveInstIR && inst.next instanceof BinaryInstIR){
                MoveInstIR move = (MoveInstIR) inst;
                BinaryInstIR binary = (BinaryInstIR) inst.next;

            }
        }
    }
}
