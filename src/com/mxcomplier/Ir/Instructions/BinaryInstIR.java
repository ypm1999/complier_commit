package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.StackSoltIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Ir.RegisterSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BinaryInstIR extends InstIR {
    private AddressIR dest;
    private OperandIR src;
    private Op op;
    public BinaryInstIR(Op op, AddressIR dest, OperandIR src) {
        this.op = op;
        this.dest = dest;
        this.src = src;
    }

    public Op getOp() {
        return op;
    }

    public void setSrc(OperandIR src) {
        this.src = src;
    }

    public OperandIR getSrc() {
        return src;
    }

    public void setDest(AddressIR dest) {
        this.dest = dest;
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
    public List<VirtualRegisterIR> getUsedVReg() {
        List<VirtualRegisterIR> tmp = getVreg(src);
        tmp.addAll(getVreg(dest));
        if (op == Op.MOD || op == Op.DIV || op == Op.MUL)
            tmp.add(RegisterSet.Vrdx);
        return tmp;
    }

    @Override
    public List<VirtualRegisterIR> getDefinedVreg() {
        List<VirtualRegisterIR> tmp = new ArrayList<>();
        if (dest instanceof VirtualRegisterIR)
            tmp.add((VirtualRegisterIR) dest);
        if (op == Op.MOD || op == Op.DIV || op == Op.MUL)
            tmp.add(RegisterSet.Vrdx);
        return tmp;
    }

    @Override
    public void replaceVreg(Map<VirtualRegisterIR, VirtualRegisterIR> renameMap) {
        dest = (AddressIR) replacedVreg(dest, renameMap);
        src = replacedVreg(src, renameMap);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", op.toString().toLowerCase(), dest, src);
    }

    public String nasmString() {
        switch (op) {
            case SHL:
            case SHR:
                if (src == RegisterSet.Vrcx)
                    return String.format("%s %s, cl", op.toString().toLowerCase(), dest);
                else
                    return String.format("%s %s, %s", op.toString().toLowerCase(), dest, src);
            case MUL:
                return String.format("mul %s", src);
            case DIV:
            case MOD:
                return String.format("div %s", src);
            default:
                return String.format("%s %s, %s", op.toString().toLowerCase(), dest, src);
        }
    }

    @Override
    public InstIR copy() {
        return new BinaryInstIR(op, (AddressIR) dest.copy(), src.copy());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public enum Op {
        ADD, SUB, MUL, DIV, MOD, SHL, SHR, AND, OR, XOR, ERROR
    }
}
