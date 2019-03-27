package com.mxcomplier.AST;

public class SuffixExprNode extends ExprNode {
    public enum SuffixOp {
        SUFFIX_INC, SUFFIX_DEC,
        NULL
    }

    private ExprNode subExpr;
    private SuffixOp suffixOp;

    public SuffixExprNode(ExprNode subExpr, SuffixOp suffixOp, Location location) {
        this.subExpr = subExpr;
        this.suffixOp = suffixOp;
        this.location = location;
    }

    public ExprNode getSubExpr() {
        return subExpr;
    }

    public SuffixOp getSuffixOp() {
        return suffixOp;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
