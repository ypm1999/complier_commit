package com.mxcomplier.Scope;

import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Type.Type;

public class VarSymbol extends Symbol {
    public ClassSymbol belongClass = null;
    public boolean isGlobalVar = false;
    public VirtualRegisterIR vReg;

    public VarSymbol(String name, Type type) {
        super(name, type);
    }

}
