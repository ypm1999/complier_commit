package com.mxcomplier.Type;

public class VoidType extends Type {
    static private VoidType instance = new VoidType();

    private VoidType() {
        this.hyperType = HyperType.VOID;
    }

    static public VoidType getInstance() {
        return instance;
    }
}
