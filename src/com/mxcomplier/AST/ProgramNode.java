package com.mxcomplier.AST;

import com.mxcomplier.Scope.Scope;

import java.util.List;

public class ProgramNode extends Node {
    private List<Node> sections;
    private Scope scope;

    public ProgramNode(List<Node> sections,
                       Scope scope,
                       Location location) {
        this.sections = sections;
        this.scope = scope;
        this.location = location;
    }

    public List<Node> getSections() {
        return sections;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
