package com.mxcomplier.backEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.CJumpInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.JumpInstIR;
import com.mxcomplier.Ir.ProgramIR;

public class Cjumpfixer extends IRScanner {

    @Override
    public void visit(BasicBlockIR node) {
        InstIR inst = node.getHead().next;
        while (inst != node.getTail()) {
            inst.accept(this);
            inst = inst.next;
        }
    }

    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs()) {
            func.accept(this);
        }
    }

    @Override
    public void visit(FuncIR node) {
        for (BasicBlockIR bb : node.getBBList()) {
            bb.accept(this);
        }
    }


    @Override
    public void visit(CJumpInstIR node) {
        if (node.getTrueBB().getFronters().size() == 1 ||
                (node.getFalseBB().getFronters().size() > 1 && node.getTrueBB().getInstNum() < node.getFalseBB().getInstNum()))
            node.reverseOp();
        node.append(new JumpInstIR(node.getFalseBB()));
        node.removeFalseBB();
    }
}
