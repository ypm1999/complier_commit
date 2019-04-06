package com.mxcomplier.AST;

public class ForStmtNode extends StmtNode {
    private ExprNode expr1, expr2, expr3;
    private StmtNode stmt;

    public ForStmtNode(ExprNode expr1, ExprNode expr2, ExprNode expr3, StmtNode stmt, Location location) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.expr3 = expr3;
        this.stmt = stmt;
        this.location = location;
    }

    public ExprNode getExpr1() {
        return expr1;
    }

    public ExprNode getExpr2() {
        return expr2;
    }

    public ExprNode getExpr3() {
        return expr3;
    }

    public StmtNode getStmt() {
        return stmt;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
