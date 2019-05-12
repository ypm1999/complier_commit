package com.mxcomplier.Ir;

import com.mxcomplier.Ir.Operands.StaticDataIR;

import java.util.ArrayList;
import java.util.List;

public class ProgramIR {
    private List<FuncIR> funcs;
    private List<StaticDataIR> staticData;

    public ProgramIR(){
        this.funcs = new ArrayList<>();
        this.staticData = new ArrayList<>();
    }

    public List<FuncIR> getFuncs() {
        return funcs;
    }

    public List<StaticDataIR> getStaticData() {
        return staticData;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
