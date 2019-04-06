package com.mxcomplier.Type;

abstract public class Type {
    protected int varSize;
    protected HyperType hyperType;

    public int getVarSize() {
        return varSize;
    }

    public HyperType getHyperType() {
        return hyperType;
    }

    @Override
    public String toString() {
        return String.format("Type(%s)", hyperType.toString());
    }

    public enum HyperType {
        INT, BOOL, STRING, VOID, NULL, ARRAY, CLASS, FUNC
    }
}
