package com.mxcomplier.AST;

import com.mxcomplier.Scope.Scope;

public class IfStmtNode extends StmtNode {
    private ExprNode judgeExpr;
    private StmtNode thenStmt, elseStmt;
    private Scope scope = new Scope();

    public IfStmtNode(ExprNode judgeExpr, StmtNode thenStmt, StmtNode elseStmt, Location location) {
        this.judgeExpr = judgeExpr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
        this.location = location;
    }

    public IfStmtNode(ExprNode judgeExpr, StmtNode thenStmt, Location location) {
        this.judgeExpr = judgeExpr;
        this.thenStmt = thenStmt;
        this.elseStmt = null;
        this.location = location;
    }

    public ExprNode getJudgeExpr() {
        return judgeExpr;
    }

    public StmtNode getElseStmt() {
        return elseStmt;
    }

    public StmtNode getThenStmt() {
        return thenStmt;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
