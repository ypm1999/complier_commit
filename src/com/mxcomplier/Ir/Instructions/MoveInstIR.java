package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoveInstIR extends InstIR {
    private AddressIR dest;
    private OperandIR src;

    public MoveInstIR(AddressIR dest, OperandIR src) {
        this.dest = dest;
        this.src = src;
    }

    public AddressIR getDest() {
        return dest;
    }

    public void setDest(AddressIR dest) {
        this.dest = dest;
    }

    public OperandIR getSrc() {
        return src;
    }

    public void setSrc(OperandIR src) {
        this.src = src;
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
        if (dest instanceof MemoryIR)
            tmp.addAll(((MemoryIR) dest).getVreg());
        return tmp;
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
        src = replacedVreg(src, renameMap);
    }

    @Override
    public InstIR copy() {
        return new MoveInstIR((AddressIR) dest.copy(), src.copy());
    }

    @Override
    public String toString() {
        return String.format("mov %s %s", dest, src);
    }

    public String nasmString() {
        return String.format("mov %s, %s", dest, src);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
