package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.OperandIR;

public class BinaryInstIR extends InstIR {
    public enum Op{
        ADD, SUB, MUL, DIV, MOD, SHL, SHR, AND, OR, XOR, ERROR
    }

    private Op op;
    private AddressIR dest;
    private OperandIR src;

    public BinaryInstIR(Op op, AddressIR dest, OperandIR src){
        this.op = op;
        this.dest = dest;
        this.src = src;
    }


    public Op getOp() {
        return op;
    }

    public OperandIR getSrc() {
        return src;
    }

    public AddressIR getDest() {
        return dest;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", op.toString().toLowerCase(), dest, src);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
