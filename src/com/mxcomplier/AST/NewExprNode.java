package com.mxcomplier.AST;

import java.util.List;

public class NewExprNode extends ExprNode {
    private TypeNode baseType;
    private List<ExprNode> dims;
    private int order;

    public NewExprNode(TypeNode baseType, List<ExprNode> dims, int order, Location location) {
        this.baseType = baseType;
        this.dims = dims;
        this.order = order;
        this.location = location;
    }

    public int getOrder() {
        return order;
    }

    public List<ExprNode> getDims() {
        return dims;
    }

    public TypeNode getBaseType() {
        return baseType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

