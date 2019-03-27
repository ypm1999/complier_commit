package com.mxcomplier.AST;

public class ContinueStmtNode extends StmtNode {

    public ContinueStmtNode(Location location){
        this.location = location;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
