package com.mxcomplier.AST;

public class ArrCallExprNode extends ExprNode {
    private ExprNode baseExpr;
    private ExprNode subscriptExpr;

    public ArrCallExprNode(ExprNode baseExpr, ExprNode subscriptExpr, Location location) {
        this.baseExpr = baseExpr;
        this.subscriptExpr = subscriptExpr;
        this.location = location;
    }

    public ExprNode getBaseExpr() {
        return baseExpr;
    }

    public ExprNode getSubscriptExpr() {
        return subscriptExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
