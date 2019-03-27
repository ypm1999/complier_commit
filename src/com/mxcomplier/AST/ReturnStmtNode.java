package com.mxcomplier.AST;

import com.mxcomplier.Type.Type;

public class ReturnStmtNode extends StmtNode {
    ExprNode returnExpr;
    Type type;

    public ReturnStmtNode(ExprNode returnExpr, Location location){
        this.location = location;
        this.returnExpr = returnExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public ExprNode getReturnExpr() {
        return returnExpr;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
