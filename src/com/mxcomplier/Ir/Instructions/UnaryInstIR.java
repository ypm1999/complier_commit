package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.StackSoltIR;

import java.util.ArrayList;
import java.util.List;

public class UnaryInstIR extends InstIR {
    public enum Op{
        NEG, INV, INC, DEC, NULL, ERROR
    }

    private Op op;
    public AddressIR dest;

    public UnaryInstIR(Op op, AddressIR dest){
        this.op = op;
        this.dest = dest;
    }

    public Op getOp() {
        return op;
    }

    public AddressIR getDest() {
        return dest;
    }

    @Override
    public List<StackSoltIR> getStackSolt() {
        List<StackSoltIR> res = new ArrayList<>();
        if (dest instanceof StackSoltIR)
            res.add((StackSoltIR) dest);
        return res;
    }

    @Override
    public String toString() {
        return String.format("%s %s", op.toString().toLowerCase(), dest);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}

