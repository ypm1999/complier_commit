package com.mxcomplier.Ir.Operands;


import com.mxcomplier.Ir.IRVisitor;

public class StackSoltIR extends MemoryIR {
    private String lable;

    public StackSoltIR(String lable){
        this.lable = lable;
    }

    public String getLable() {
        return lable;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
