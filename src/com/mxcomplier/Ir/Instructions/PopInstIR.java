package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;

public class PopInstIR extends InstIR {
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
