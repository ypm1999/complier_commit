package com.mxcomplier.AST;

public class BoolConstExprNode extends ConstExprNode {

    public enum BoolValue {
        TRUE, FALSE,
        NULL
    }

    private BoolValue value;

    public BoolConstExprNode(BoolValue value, Location location) {
        this.value = value;
        this.location = location;
    }

    public BoolValue getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
