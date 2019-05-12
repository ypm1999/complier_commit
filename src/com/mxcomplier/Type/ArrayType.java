package com.mxcomplier.Type;

import com.mxcomplier.Config;

public class ArrayType extends Type {
    private Type baseType;

    public ArrayType(Type baseType) {
        this.baseType = baseType;
        this.hyperType = HyperType.ARRAY;
        this.varSize = Config.getREGSIZE();
    }

    public Type getBaseType() {
        return baseType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayType)
            return baseType.equals(((ArrayType) obj).baseType);
        else
            return false;
    }

    @Override
    public String toString() {
        return String.format("ArrayType(%s)", baseType.toString());
    }
}
