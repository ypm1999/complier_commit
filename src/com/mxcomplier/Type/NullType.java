package com.mxcomplier.Type;

public class NullType extends Type {
    static private NullType instance = new NullType();

    private NullType() {
        this.hyperType = HyperType.NULL;
    }

    static public NullType getInstance() {
        return instance;
    }
}
