package com.mxcomplier.AST;

public class StringConstExprNode extends ConstExprNode {
    private String string;

    public StringConstExprNode(String string, Location location) {
        this.string = string;
        this.location = location;
    }

    public String getString() {
        return string;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
