package com.mxcomplier.AST;

import java.util.List;

public class FuncCallExprNode extends ExprNode {
    private ExprNode baseExpr;
    private List<ExprNode> argumentList;
    private String funcName;

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

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
