package com.mxcomplier.Scope;

import com.mxcomplier.Type.Type;

abstract public class Symbol {
    private boolean isOutputIrrelevant = false;
    private String name;
    private Type type;

    protected Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isOutputIrrelevant() {
        return isOutputIrrelevant;
    }

    public void setOutputIrrelevant(boolean outputIrrelevant) {
        isOutputIrrelevant = outputIrrelevant;
    }
}
