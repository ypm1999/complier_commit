package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Error.IRError;
import com.mxcomplier.Ir.IRVisitor;

public class PhysicalRegisterIR extends RegisterIR {

    static private int ID = 0;
    private int id;

    public PhysicalRegisterIR(String label){
        this.lable = label;
        this.id = ++ID;
    }

    @Override
    public OperandIR copy() {
        throw new IRError("copy PhyReg");
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return this.lable;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
