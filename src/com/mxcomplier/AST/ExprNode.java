package com.mxcomplier.AST;

import com.mxcomplier.Type.Type;

abstract public class ExprNode extends Node {
    private Type type;
    private boolean leftValue;

    public void setType(Type type) {
        this.type = type;
    }

    public void setLeftValue(boolean leftValue) {
        this.leftValue = leftValue;
    }

    public Type getType() {
        return type;
    }

    public boolean isLeftValue() {
        return leftValue;
    }
}
