package com.mxcomplier.AST;

abstract public class Node {
    protected Location location;

    private boolean isOutputIrrelevant = false;

    public boolean isOutputIrrelevant() {
        return isOutputIrrelevant;
    }

    public void setOutputIrrelevant(boolean outputIrrelevant) {
        isOutputIrrelevant = outputIrrelevant;
    }

    public Location getLocation() {
        return location;
    }

    abstract public void accept(ASTVisitor visitor);
}
