package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.PhysicalRegisterIR;

public class PushInstIR extends InstIR {

    PhysicalRegisterIR src;
    public PushInstIR(PhysicalRegisterIR reg){
        this.src = reg;
    }

    @Override
    public String toString() {
        return "push " + src;
    }

    public String nasmString(){
        return toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
