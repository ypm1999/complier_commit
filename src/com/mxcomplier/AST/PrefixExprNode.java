package com.mxcomplier.AST;

public class PrefixExprNode extends ExprNode {

    public enum PrefixOp {
        PREFIX_INC, PREFIX_DEC,
        PLUS, MINUS, NOT, INV,
        NULL
    }

    private ExprNode subExpr;
    private PrefixOp prefixOp;

    public PrefixExprNode(ExprNode subExpr, PrefixOp prefixOp, Location location) {
        this.subExpr = subExpr;
        this.prefixOp = prefixOp;
        this.location = location;
    }

    public ExprNode getSubExpr() {
        return subExpr;
    }

    public PrefixOp getPrefixOp() {
        return prefixOp;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
