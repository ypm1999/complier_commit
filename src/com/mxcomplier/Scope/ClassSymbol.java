package com.mxcomplier.Scope;

import com.mxcomplier.AST.ClassDefNode;
import com.mxcomplier.Type.ClassType;

public class ClassSymbol extends Symbol{
    private Scope scope;

    public ClassSymbol(String name, ClassDefNode node){
        super(name, new ClassType(name));
        this.scope = node.getScope();
    }

    public ClassSymbol(String name, Scope scope){
        super(name, new ClassType(name));
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }
}
