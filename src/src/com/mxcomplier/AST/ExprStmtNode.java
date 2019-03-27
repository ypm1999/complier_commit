package com.mxcomplier.AST;

public class ExprStmtNode extends StmtNode {
    private ExprNode expr;

    public ExprStmtNode(ExprNode expr, Location location){
        this.expr = expr;
        this.location = location;
    }

    public ExprNode getExpr() {
        return expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
