package com.mxcomplier.AST;

import com.mxcomplier.Scope.Symbol;

public class IdentExprNode extends ExprNode {
    private String name;
    private boolean isFunc = false;
    private boolean isVar = false;
    private Symbol symbol;

    public IdentExprNode(String name, Location location) {
        this.name = name;
        this.location = location;
    }


    public String getName() {
        return name;
    }

    public boolean isFunc() {
        return isFunc;
    }

    public boolean isVar() {
        return isVar;
    }

    public void setFunc(boolean func) {
        isFunc = func;
    }

    public void setVar(boolean var) {
        isVar = var;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
