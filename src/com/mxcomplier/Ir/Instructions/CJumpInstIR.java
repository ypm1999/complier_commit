package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.List;
import java.util.Map;

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

    public void swap(){
        switch (op){
            case L: op = Op.G; break;
            case G: op = Op.L; break;
            case LE: op = Op.GE; break;
            case GE: op = Op.LE; break;
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
    public void replaceVreg(Map<VirtualRegisterIR, VirtualRegisterIR> renameMap){
        lhs = replacedVreg(lhs, renameMap);
        rhs = replacedVreg(rhs, renameMap);
    }


    public String toString() {
        return String.format("cjmp if(%s %s %s) goto %s else goto %s", lhs, op, rhs, trueBB, falseBB);
    }

    public String nasmString() {
        String str1 = "cmp " + lhs + ", " + rhs;
        String str2 = 'j' + op.toString().toLowerCase() + ' ' + trueBB;
        String str3 = "jmp " + falseBB;
        return str1 + "\n" + str2 + "\n" + str3;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
