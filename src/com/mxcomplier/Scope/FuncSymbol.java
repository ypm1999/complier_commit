package com.mxcomplier.Scope;

import com.mxcomplier.AST.FuncDefNode;
import com.mxcomplier.Type.FuncType;
import com.mxcomplier.Type.Type;

import java.util.List;

public class FuncSymbol extends Symbol{
    private Type returnType;
    private Scope scope;
    private List<Type> parameters;


    public FuncSymbol(String name, Type returnType, Scope scope, List<Type> args){
        super(name, new FuncType(name));
        this.scope = scope;
        this.returnType = returnType;
        this.parameters = args;
    }

    public Scope getScope(){
        return scope;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getParameters() {
        return parameters;
    }
}
