package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.StackSoltIR;

import java.util.ArrayList;
import java.util.List;

public class CompInstIR extends InstIR {

    public OperandIR lhs, rhs;

    public CompInstIR(OperandIR lhs, OperandIR rhs){

        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public List<StackSoltIR> getStackSolt() {
        List<StackSoltIR> res = new ArrayList<>();
        if (lhs instanceof StackSoltIR)
            res.add((StackSoltIR) lhs);
        if (rhs instanceof StackSoltIR)
            res.add((StackSoltIR) rhs);
        return res;
    }


    public String nasmString() {
        return "cmp " + lhs + ", " + rhs;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
