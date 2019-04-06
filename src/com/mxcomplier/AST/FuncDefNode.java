package com.mxcomplier.AST;

import java.util.List;

final public class FuncDefNode extends Node {
    private String name;
    private TypeNode returnType;
    private boolean isConstructor;
    private List<VarDefNode> parameters;
    private CompStmtNode funcBody;

    public FuncDefNode(String name,
                       TypeNode returnType,
                       List<VarDefNode> parameters,
                       CompStmtNode funcBody,
                       Location location) {
        this.name = name;
        this.returnType = returnType;
        this.isConstructor = (returnType == null);
        this.parameters = parameters;
        this.funcBody = funcBody;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public boolean getIsConstructor() {
        return isConstructor;
    }

    public List<VarDefNode> getParameters() {
        return parameters;
    }

    public CompStmtNode getFuncBody() {
        return funcBody;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
