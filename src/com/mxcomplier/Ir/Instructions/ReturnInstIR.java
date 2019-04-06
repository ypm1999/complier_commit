package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.RegisterIR;

public class ReturnInstIR extends BranchInstIR {
    private RegisterIR dest;

    public ReturnInstIR(RegisterIR dest){
        this.dest = dest;
    }

    public RegisterIR getDest() {
        return dest;
    }


    @Override
    public String toString() {
        return String.format("ret %s", dest);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
