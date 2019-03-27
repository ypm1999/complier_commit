package com.mxcomplier.Type;

public class FuncType extends Type{
    private String name;

    public FuncType(String name){
        this.name = name;
        this.hyperType = HyperType.FUNC;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FuncType)
            return name.equals(((FuncType) obj).name);
        else
            return false;
    }

    @Override
    public String toString() {
        return String.format("FuncType(%s)", name.toString());
    }
}
