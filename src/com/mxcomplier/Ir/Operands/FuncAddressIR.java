package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;

public class FuncAddressIR extends ConstantIR {
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
