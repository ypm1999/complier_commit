package com.mxcomplier.AST;

import com.mxcomplier.Ir.FuncIR;

import java.util.List;

final public class FuncDefNode extends Node {
    private String name;
    private TypeNode returnType;
    private boolean isConstructor;
    private List<VarDefNode> parameters;
    private CompStmtNode funcBody;
    private FuncIR funcIR;

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

    public void setName(String name) {
        this.name = name;
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

    public FuncIR getFuncIR() {
        return funcIR;
    }

    public void setFuncIR(FuncIR funcIR) {
        this.funcIR = funcIR;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
