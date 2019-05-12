package com.mxcomplier.Scope;

import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Type.FuncType;
import com.mxcomplier.Type.Type;

import java.util.List;

public class FuncSymbol extends Symbol {
    private Type returnType;
    private Scope scope;
    private List<Type> parameters;
    private boolean isConstructor;
    private ClassSymbol belongClass;


    public FuncSymbol(String name, Type returnType, Scope scope, List<Type> args, ClassSymbol belongClass) {
        super(name, new FuncType(name));
        this.scope = scope;
        this.returnType = returnType;
        this.parameters = args;
        isConstructor = false;
        this.belongClass = belongClass;
    }

    public Scope getScope() {
        return scope;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public ClassSymbol getBelongClass() {
        return belongClass;
    }

    public void setConstructor(boolean constructor) {
        isConstructor = constructor;
    }
}
