package com.mxcomplier.AST;

public class BinaryExprNode extends ExprNode {

    public enum Op {
        MUL, DIV, MOD, PLUS, MINUS, LSH, RSH,
        LESS, LARGE, LESS_EQUAL, LARGE_EQUAL, EQUAL, UNEQUAL,
        AND, XOR, OR, ANDAND, OROR
    }

    private ExprNode leftExpr, rightExpr;
    private Op op;

    public BinaryExprNode(ExprNode leftExpr, ExprNode rightExpr, Op op, Location location) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.location = location;
        this.op = op;
    }

    public ExprNode getLeftExpr() {
        return leftExpr;
    }

    public ExprNode getRightExpr() {
        return rightExpr;
    }

    public Op getOp() {
        return op;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
