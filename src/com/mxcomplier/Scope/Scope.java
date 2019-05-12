package com.mxcomplier.Scope;

import com.mxcomplier.AST.Location;
import com.mxcomplier.Error.ComplierError;
import com.mxcomplier.Error.IRError;
import com.mxcomplier.Type.ClassType;
import com.mxcomplier.Type.FuncType;

import java.util.HashMap;

public class Scope {
    private Scope parent;
    private HashMap<String, Symbol> identMap = new HashMap<>();

    public Scope() {
        this.parent = null;
    }

    public Scope(Scope parent) {
        this.parent = parent;
    }


    public void put(Symbol symbol, Location location) {
        if (identMap.containsKey(symbol.getName()))
            throw new ComplierError(location, String.format("Symbol %s is decleared before", symbol.getName()));
        else identMap.put(symbol.getName(), symbol);
    }

    public void put(Symbol symbol) {
        if (identMap.containsKey(symbol.getName()))
            throw new ComplierError(String.format("Symbol %s is decleared before", symbol.getName()));
        else identMap.put(symbol.getName(), symbol);
    }

    public Symbol getSelf(String name, Location location) {
        if (!identMap.containsKey(name))
            throw new ComplierError(location, String.format("Symbol %s is not decleared", name));
        else
            return identMap.get(name);
    }

    public Symbol get(String name, Location location) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.get(name, location);
            else
                throw new ComplierError(location, String.format("Symbol %s is not decleared", name));
        else
            return identMap.get(name);
    }

    public ClassSymbol getClass(String name, Location location) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getClass(name, location);
            else
                throw new ComplierError(location, String.format("Class %s is not decleared", name));
        else {
            Symbol symbol = identMap.get(name);
            if (symbol instanceof ClassSymbol)
                return (ClassSymbol) symbol;
            else
                throw new ComplierError(location, String.format("Class %s is not decleared as class", name));
        }
    }

    public FuncSymbol getFunc(String name, Location location) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getFunc(name, location);
            else
                throw new ComplierError(location, String.format("Function %s is not decleared", name));
        else {
            Symbol symbol = identMap.get(name);
            if (symbol instanceof FuncSymbol)
                return (FuncSymbol) symbol;
            else
                throw new ComplierError(location, String.format("Function %s is not decleared as class", name));
        }
    }

    public Symbol get(String name) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.get(name);
            else
                throw new IRError(String.format("get %s", name));
        else
            return identMap.get(name);
    }


    public VarSymbol getVar(String name, Location location) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getVar(name, location);
            else
                throw new ComplierError(location, String.format("Variable %s is not decleared", name));
        else {
            Symbol symbol = identMap.get(name);
            if (symbol instanceof VarSymbol)
                return (VarSymbol) symbol;
            else
                throw new ComplierError(location, String.format("Variable %s is not decleared as class", name));
        }
    }

    public ClassSymbol getClass(String name) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getClass(name);
            else
                throw new IRError("IR getClass1");
        else {
            Symbol symbol = identMap.get(name);
            if (symbol instanceof ClassSymbol)
                return (ClassSymbol) symbol;
            else
                throw new IRError("IR getClass2");
        }

    }

    public FuncSymbol getFunc(String name) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getFunc(name);
            else
                throw new IRError("IR getFunc1");
        else {
            Symbol symbol = identMap.get(name);
            if (symbol instanceof FuncSymbol)
                return (FuncSymbol) symbol;
            else
                throw new IRError("IR getFunc2");
        }
    }


    public VarSymbol getVar(String name) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getVar(name);
            else
                throw new IRError("IR getVar1");
        else {
            Symbol symbol = identMap.get(name);
            if (symbol instanceof VarSymbol)
                return (VarSymbol) symbol;
            else
                throw new IRError("IR getVar2");
        }
    }

    public ClassSymbol tryGetClass(String name) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getClass(name);
            else
                return null;
        else {
            Symbol symbol = identMap.get(name);
            if (symbol.getType() instanceof ClassType)
                return (ClassSymbol) symbol;
            else
                return null;
        }

    }

    public FuncSymbol tryGetFunc(String name) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getFunc(name);
            else
                return null;
        else {
            Symbol symbol = identMap.get(name);
            if (symbol.getType() instanceof FuncType)
                return (FuncSymbol) symbol;
            else
                return null;
        }
    }


    public VarSymbol tryGetVar(String name) {
        if (!identMap.containsKey(name))
            if (parent != null)
                return parent.getVar(name);
            else
                return null;
        else {
            Symbol symbol = identMap.get(name);
            if (symbol instanceof VarSymbol)
                return (VarSymbol) symbol;
            else
                return null;
        }
    }


    public Scope getParent() {
        return parent;
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public boolean isTop() {
        return parent == null;
    }
}
