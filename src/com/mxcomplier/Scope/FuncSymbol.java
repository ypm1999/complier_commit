package com.mxcomplier.Scope;

import com.mxcomplier.Type.FuncType;
import com.mxcomplier.Type.Type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FuncSymbol extends Symbol {
    private Type returnType;
    private Scope scope;
    private List<Type> parameters;
    private boolean isConstructor;
    private ClassSymbol belongClass;
    private Set<Symbol> dependence = new HashSet<>();


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

    public Set<Symbol> getDependence() {
        return dependence;
    }

    public void addDependence(Symbol symbol){
        dependence.add(symbol);
    }

    public void setConstructor(boolean constructor) {
        isConstructor = constructor;
    }
}
