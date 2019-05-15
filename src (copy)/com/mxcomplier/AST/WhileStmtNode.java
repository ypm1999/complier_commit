package com.mxcomplier.AST;

import com.mxcomplier.Scope.Scope;

public class WhileStmtNode extends StmtNode {
    private ExprNode judgeExpr;
    private StmtNode stmt;
    private Scope scope = new Scope();

    public WhileStmtNode(ExprNode judgeExpr, StmtNode stmt, Location location) {
        this.judgeExpr = judgeExpr;
        this.stmt = stmt;
        this.location = location;
    }

    public StmtNode getStmt() {
        return stmt;
    }

    public ExprNode getJudgeExpr() {
        return judgeExpr;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
