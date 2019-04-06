package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.OperandIR;

public class MoveInstIR extends InstIR {
    private AddressIR dest;
    private OperandIR src;

    public MoveInstIR(AddressIR dest, OperandIR src){
        this.dest = dest;
        this.src = src;
    }

    public AddressIR getDest() {
        return dest;
    }

    public OperandIR getSrc() {
        return src;
    }


    @Override
    public String toString() {
        return String.format("mov %s %s", dest, src);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
