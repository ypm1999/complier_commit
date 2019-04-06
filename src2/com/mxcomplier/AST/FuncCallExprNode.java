package com.mxcomplier.AST;

import java.util.List;

public class FuncCallExprNode extends ExprNode {
    private ExprNode baseExpr;
    private List<ExprNode> argumentList;

    public FuncCallExprNode(ExprNode baseExpr, List<ExprNode> argumentList, Location location) {
        this.baseExpr = baseExpr;
        this.argumentList = argumentList;
        this.location = location;
    }

    public ExprNode getBaseExpr() {
        return baseExpr;
    }

    public List<ExprNode> getArgumentList() {
        return argumentList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
