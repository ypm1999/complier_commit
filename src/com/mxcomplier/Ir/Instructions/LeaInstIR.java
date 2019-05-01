package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.MemoryIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.StackSoltIR;

import java.util.ArrayList;
import java.util.List;

public class LeaInstIR extends InstIR {
    public AddressIR dest;
    public AddressIR src;

    public LeaInstIR(AddressIR dest, MemoryIR src){
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
