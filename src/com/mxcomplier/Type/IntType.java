package com.mxcomplier.Type;

public class IntType extends Type{
    static private IntType instance = new IntType();

    private IntType(){
        this.hyperType = HyperType.INT;
    }

    static public IntType getInstance() {
        return instance;
    }
}
