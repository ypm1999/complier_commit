package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.OperandIR;

public class ReturnInstIR extends BranchInstIR {

    private OperandIR src;

    public ReturnInstIR(OperandIR src){
        this.src = src;
    }


    public OperandIR getSrc() {
        return src;
    }

    @Override
    public String toString() {
        return "leave\nret";
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
