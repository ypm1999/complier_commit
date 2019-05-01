package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;

public class ImmediateIR extends ConstantIR {
    private long value;

    public ImmediateIR(long value){
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
