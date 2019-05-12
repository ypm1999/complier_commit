package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Ir.RegisterSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReturnInstIR extends BranchInstIR {

    private OperandIR src;

    public ReturnInstIR() {
        this.src = null;
    }

    public ReturnInstIR(OperandIR src) {
        this.src = src;
    }

//    public List<VirtualRegisterIR> getIRUsedVReg(){
//        if (src != null)
//            return getVreg(src);
//        else
//            return new ArrayList<>();
//    }


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
    public InstIR copy() {
        if (src != null)
            return new ReturnInstIR(src.copy());
        else
            return new ReturnInstIR();
    }

    @Override
    public String toString() {
        return "leave\nret";
    }

    @Override
    public void bbRename(Map<BasicBlockIR, BasicBlockIR> bbRenameMap) {
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
