package com.mxcomplier.AST;

public class IdentExprNode extends ExprNode {
    private String name;

    public IdentExprNode(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
