package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Error.IRError;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.List;
import java.util.Map;

public class CJumpInstIR extends BranchInstIR {
    public enum Op {
        L, G, LE, GE, E, NE, ERROR
    }

    private Op op;
    public OperandIR lhs, rhs;
    private BasicBlockIR trueBB, falseBB;

    public CJumpInstIR(Op op, OperandIR lhs, OperandIR rhs, BasicBlockIR trueBB, BasicBlockIR falseBB) {
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

    public void removeFalseBB() {
        falseBB = null;
    }

    public void reverseOp(){
        BasicBlockIR tmp = trueBB;
        trueBB = falseBB;
        falseBB = tmp;
        switch (op) {
            case L:
                op = Op.GE;
                break;
            case G:
                op = Op.LE;
                break;
            case LE:
                op = Op.G;
                break;
            case GE:
                op = Op.L;
                break;
            case E:
                op = Op.NE;
                break;
            case NE:
                op = Op.E;
                break;
            default: throw new IRError("OP in Cjump");
        }
    }

    public void swap() {
        switch (op) {
            case L:
                op = Op.G;
                break;
            case G:
                op = Op.L;
                break;
            case LE:
                op = Op.GE;
                break;
            case GE:
                op = Op.LE;
                break;
        }
        OperandIR tmp = lhs;
        lhs = rhs;
        rhs = tmp;
    }

    @Override
    public List<VirtualRegisterIR> getUsedVReg() {
        List<VirtualRegisterIR> regs = getVreg(lhs);
        regs.addAll(getVreg(rhs));
        return regs;
    }

    @Override
    public void replaceVreg(Map<VirtualRegisterIR, VirtualRegisterIR> renameMap) {
        lhs = replacedVreg(lhs, renameMap);
        rhs = replacedVreg(rhs, renameMap);
    }

    @Override
    public InstIR copy() {
        return new CJumpInstIR(op, lhs.copy(), rhs.copy(), trueBB, falseBB);
    }

    public String toString() {
        if (falseBB == null)
            return String.format("cjmp if(%s %s %s) goto %s", lhs, op, rhs, trueBB);
        else
            return String.format("cjmp if(%s %s %s) goto %s else %s", lhs, op, rhs, trueBB, falseBB);
    }

    public String nasmString() {
        String str1 = "cmp " + lhs + ", " + rhs;
        String str2 = 'j' + op.toString().toLowerCase() + ' ' + trueBB;
        return str1 + "\n" + str2;
    }

    @Override
    public void bbRename(Map<BasicBlockIR, BasicBlockIR> bbRenameMap) {
        trueBB = bbRenameMap.getOrDefault(trueBB, trueBB);
        falseBB = bbRenameMap.getOrDefault(falseBB, falseBB);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
