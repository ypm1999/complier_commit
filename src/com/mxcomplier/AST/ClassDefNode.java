package com.mxcomplier.AST;

import com.mxcomplier.Scope.Scope;

import java.util.List;

public class ClassDefNode extends Node {
    private String name;
    private List<VarDefNode> memberDefs;
    private List<FuncDefNode> funcDefs;
    private Scope scope;

    public ClassDefNode(String name,
                        List<VarDefNode> memberDefs,
                        List<FuncDefNode> funcDefs,
                        Scope scope,
                        Location location) {
        this.name = name;
        this.memberDefs = memberDefs;
        this.funcDefs = funcDefs;
        this.location = location;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public List<VarDefNode> getMemberDefs() {
        return memberDefs;
    }

    public List<FuncDefNode> getFuncDefs() {
        return funcDefs;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
