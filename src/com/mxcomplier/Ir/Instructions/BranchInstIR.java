package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;

abstract public class BranchInstIR extends InstIR {

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
