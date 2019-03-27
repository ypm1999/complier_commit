package com.mxcomplier.AST;

public class NullExprNode extends ConstExprNode {

    public NullExprNode(Location location) {
        this.location = location;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
