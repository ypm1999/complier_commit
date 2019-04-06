package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;

public class PhysicalRegisterIR extends RegisterIR {
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
