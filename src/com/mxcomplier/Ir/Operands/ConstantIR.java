package com.mxcomplier.Ir.Operands;

abstract public class ConstantIR extends RegisterIR {
    @Override
    public OperandIR copy() {
        return this;
    }
}
