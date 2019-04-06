package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.OperandIR;

public class CJumpInstIR extends BranchInstIR {
    public enum Op{
        L, G, LE, GE, EQ, NEQ, ERROR
    }

    private Op op;
    private OperandIR lhs, rhs;
    private BasicBlockIR trueBB, falseBB;

    public CJumpInstIR(Op op, OperandIR lhs, OperandIR rhs, BasicBlockIR trueBB, BasicBlockIR falseBB){
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        this.trueBB = trueBB;
        this.falseBB = falseBB;
    }

    public Op getOp() {
        return op;
    }

    public OperandIR getLhs() {
        return lhs;
    }

    public OperandIR getRhs() {
        return rhs;
    }

    public BasicBlockIR getTrueBB() {
        return trueBB;
    }

    public BasicBlockIR getFalseBB() {
        return falseBB;
    }


    @Override
    public String toString() {
        return String.format("cjmp if(%s %s %s) goto %s else goto %s", lhs, op, rhs, trueBB, falseBB);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
