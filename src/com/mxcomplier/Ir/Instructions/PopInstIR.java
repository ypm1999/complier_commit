package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.MemoryIR;
import com.mxcomplier.Ir.Operands.RegisterIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PopInstIR extends InstIR {

    private RegisterIR dest;

    public PopInstIR(RegisterIR dest){
        this.dest = dest;
    }

    public RegisterIR getDest() {
        return dest;
    }

    @Override
    public List<VirtualRegisterIR> getDefinedVreg() {
        List<VirtualRegisterIR> tmp = new ArrayList<>();
        if (dest instanceof VirtualRegisterIR)
            tmp.add((VirtualRegisterIR)dest);
        return tmp;
    }

    @Override
    public void replaceVreg(Map<VirtualRegisterIR, VirtualRegisterIR> renameMap){
        dest = (RegisterIR) replacedVreg(dest, renameMap);
    }

    @Override
    public String toString() {
        return "pop " + dest;
    }

    public String nasmString(){
        return toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
