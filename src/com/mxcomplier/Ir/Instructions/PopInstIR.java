package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;

public class PopInstIR extends InstIR {

    @Override
    public String toString() {
        return "pop";
    }

    public String nasmString(){
        return toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
