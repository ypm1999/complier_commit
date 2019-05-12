package com.mxcomplier.Ir.Operands;


import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.RegisterSet;

public class StackSoltIR extends MemoryIR {
    private String lable;

    public StackSoltIR(String lable) {
        super(RegisterSet.Vrbp);
        this.lable = lable;
    }

    public String getLable() {
        return lable;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
