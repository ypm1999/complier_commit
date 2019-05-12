package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaInstIR extends InstIR {
    public AddressIR dest;
    public AddressIR src;

    public LeaInstIR(AddressIR dest, AddressIR src){
        this.dest = dest;
        this.src = src;
    }

    public AddressIR getDest() {
        return dest;
    }

    public OperandIR getSrc() {
        return src;
    }

    public void setSrc(MemoryIR src) {
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
            tmp.add((VirtualRegisterIR)dest);
        return tmp;
    }


    @Override
    public void replaceVreg(Map<VirtualRegisterIR, VirtualRegisterIR> renameMap){
        dest = (AddressIR) replacedVreg(dest, renameMap);
        src = (AddressIR) replacedVreg(src, renameMap);
    }

    @Override
    public InstIR copy() {
        return new LeaInstIR((RegisterIR) dest.copy(), (AddressIR) src.copy());
    }

    @Override
    public String toString() {
        return String.format("lea %s %s", dest, src);
    }

    public String nasmString() {
        return String.format("lea %s, %s", dest, src);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
