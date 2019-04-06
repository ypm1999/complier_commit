package com.mxcomplier.AST;

public class VarDefNode extends Node {

    private TypeNode type;
    private String name;
    private ExprNode initExpr;
    private boolean isMemberDef;
    private boolean isFuncArgs;

    public VarDefNode(TypeNode type, String name, ExprNode initExpr, Location location) {
        this.type = type;
        this.name = name;
        this.initExpr = initExpr;
        this.location = location;
        this.isMemberDef = false;
        this.isFuncArgs = false;
    }

    public String getName() {
        return name;
    }

    public ExprNode getInitExpr() {
        return initExpr;
    }

    public TypeNode getType() {
        return type;
    }

    public boolean isMemberDef() {
        return isMemberDef;
    }

    public void setMemberDef(boolean MemberDef) {
        this.isMemberDef = MemberDef;
    }

    public boolean isFuncArgs() {
        return isFuncArgs;
    }

    public void setFuncArgs(boolean funcArgs) {
        isFuncArgs = funcArgs;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }//node
}