package com.mxcomplier.Ir.Instructions;

public class EmptyInstIR extends InstIR{

    @Override
    public InstIR copy() {
        return new EmptyInstIR();
    }
}
