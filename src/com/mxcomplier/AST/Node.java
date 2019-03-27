package com.mxcomplier.AST;

abstract public class Node {
    protected Location location;

    public Location getLocation() {
        return location;
    }

    abstract public void accept(ASTVisitor visitor);
}
