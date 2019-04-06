package com.mxcomplier.Type;

public class BoolType extends Type{
    static private BoolType instance = new BoolType();

    private BoolType(){
        this.hyperType = HyperType.BOOL;
    }

    static public BoolType getInstance() {
        return instance;
    }
}
