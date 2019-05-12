package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.StackSoltIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnaryInstIR extends InstIR {
    public enum Op {
        NEG, INV, INC, DEC, NULL, ERROR
    }

    private Op op;
    public AddressIR dest;

    public UnaryInstIR(Op op, AddressIR dest) {
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
    public List<VirtualRegisterIR> getUsedVReg() {
        return getVreg(dest);
    }

    @Override
    public List<VirtualRegisterIR> getDefinedVreg() {
        List<VirtualRegisterIR> tmp = new ArrayList<>();
        if (dest instanceof VirtualRegisterIR)
            tmp.add((VirtualRegisterIR) dest);
        return tmp;
    }

    @Override
    public void replaceVreg(Map<VirtualRegisterIR, VirtualRegisterIR> renameMap) {
        dest = (AddressIR) replacedVreg(dest, renameMap);
    }

    @Override
    public String toString() {
        return String.format("%s %s", op.toString().toLowerCase(), dest);
    }


    @Override
    public InstIR copy() {
        return new UnaryInstIR(op, (AddressIR) dest.copy());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}

