package com.mxcomplier.AST;

public class MemberCallExprNode extends ExprNode {

    private ExprNode baseExpr;
    private String memberName;

    public MemberCallExprNode(ExprNode baseExpr, String memberName, Location location) {
        this.baseExpr = baseExpr;
        this.memberName = memberName;
        this.location = location;
    }

    public ExprNode getBaseExpr() {
        return baseExpr;
    }

    public String getMemberName() {
        return memberName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
