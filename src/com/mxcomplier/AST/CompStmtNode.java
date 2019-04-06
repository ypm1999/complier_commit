package com.mxcomplier.AST;

import com.mxcomplier.Scope.Scope;

import java.util.List;

public class CompStmtNode extends StmtNode {
    private List<Node> stmtlist;
    private Scope scope;

    public CompStmtNode(List<Node> stmtlist, Scope scope, Location location) {
        this.stmtlist = stmtlist;
        this.scope = scope;
        this.location = location;
    }

    public List<Node> getStmtlist() {
        return stmtlist;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
