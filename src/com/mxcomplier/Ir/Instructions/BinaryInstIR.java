package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.StackSoltIR;

import java.util.ArrayList;
import java.util.List;

public class BinaryInstIR extends InstIR {
    public enum Op{
        ADD, SUB, MUL, DIV, MOD, SHL, SHR, AND, OR, XOR, ERROR
    }

    private Op op;
    public AddressIR dest;
    public OperandIR src;

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
    public List<StackSoltIR> getStackSolt() {
        List<StackSoltIR> res = new ArrayList<>();
        if (dest instanceof StackSoltIR)
            res.add((StackSoltIR) dest);
        if (src instanceof StackSoltIR)
            res.add((StackSoltIR) src);
        return res;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", op.toString().toLowerCase(), dest, src);
    }

    public String nasmString() {
        switch (op){
            case MUL:
                return String.format("mul %s", src);
            case DIV:
            case MOD:
                return String.format("mov rdx,   0\ndiv %s", src);
            default:
                return String.format("%s %s, %s", op.toString().toLowerCase(), dest, src);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
