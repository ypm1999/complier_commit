package com.mxcomplier.AST;

import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

public class IdentExprNode extends ExprNode {
    private String name;
    private boolean isFunc = false;
    private boolean isVar = false;

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


    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
