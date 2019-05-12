package com.mxcomplier.AST;

import com.mxcomplier.Type.Type;

public class TypeNode extends Node {
    private Type type;

    public TypeNode(Type type, Location location) {
        this.type = type;
        this.location = location;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
