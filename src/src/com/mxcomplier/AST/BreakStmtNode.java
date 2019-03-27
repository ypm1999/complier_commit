package com.mxcomplier.AST;

public class BreakStmtNode extends StmtNode {

    public BreakStmtNode(Location location){
        this.location = location;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
