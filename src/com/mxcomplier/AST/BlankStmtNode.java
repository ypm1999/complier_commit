package com.mxcomplier.AST;

public class BlankStmtNode extends StmtNode {

    public BlankStmtNode(Location location) {
        this.location = location;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
