package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;

public class PhysicalRegisterIR extends RegisterIR {

    public PhysicalRegisterIR(String label){
        this.lable = label;
    }

    @Override
    public String toString() {
        return this.lable;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
