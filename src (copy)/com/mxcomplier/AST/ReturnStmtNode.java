package com.mxcomplier.AST;

public class ReturnStmtNode extends StmtNode {
    private ExprNode returnExpr;

    public ReturnStmtNode(ExprNode returnExpr, Location location) {
        this.location = location;
        this.returnExpr = returnExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ExprNode getReturnExpr() {
        return returnExpr;
    }
}
