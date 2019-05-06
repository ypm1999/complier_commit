package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Ir.RegisterSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnInstIR extends BranchInstIR {

    private OperandIR src;

    public ReturnInstIR(){
        this.src = null;
    }

    public ReturnInstIR(OperandIR src){
        this.src = src;
    }

    @Override
    public List<VirtualRegisterIR> getUsedVReg() {
        if (src != null)
            return getVreg(RegisterSet.Vrax);
        else
            return new ArrayList<>();
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
