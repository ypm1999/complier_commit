package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;

public class VirtualRegisterIR extends RegisterIR {
    static private int vRegId = 0;

    private int id;
    public MemoryIR memory;
    public VirtualRegisterIR(String label){
        this.id = ++vRegId;
        this.lable = label;
        this.memory = new StackSoltIR(lable + "_solt");
    }

    public int getId() {
        return id;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("vReg%d_%s", id, lable);
    }
}
