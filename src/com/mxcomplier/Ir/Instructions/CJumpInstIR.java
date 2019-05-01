package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.OperandIR;

public class CJumpInstIR extends BranchInstIR {
    public enum Op{
        L, G, LE, GE, E, NE, ERROR
    }

    private Op op;
    public OperandIR lhs, rhs;
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



    public String toString() {
        return String.format("cjmp if(%s %s %s) goto %s else goto %s", lhs, op, rhs, trueBB, falseBB);
    }

    public String nasmString() {
        return 'j' + op.toString().toLowerCase() + ' ' + trueBB + "\njmp " + falseBB;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
