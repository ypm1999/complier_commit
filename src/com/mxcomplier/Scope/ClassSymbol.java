package com.mxcomplier.Scope;

import com.mxcomplier.AST.ClassDefNode;
import com.mxcomplier.Config;
import com.mxcomplier.Type.ClassType;

import java.util.HashMap;
import java.util.Map;

public class ClassSymbol extends Symbol {
    private Scope scope;
    private int offset = 0;
    private Map<String, Integer> varOffset = new HashMap<>();

    public ClassSymbol(String name, ClassDefNode node) {
        super(name, new ClassType(name));
        this.scope = node.getScope();
    }

    public ClassSymbol(String name, Scope scope) {
        super(name, new ClassType(name));
        this.scope = scope;
    }

    public void addVar(String name){
        if (scope.getVar(name) != null){
            varOffset.put(name, offset);
            offset += Config.getREGSIZE();
        }
        else
            assert false;
    }

    public int getVarOffset(String name){
        if (varOffset.containsKey(name))
            return varOffset.get(name);
        else
            assert false;
        return -1;
    }

    public int getSize(){
        return offset;
    }

    public Scope getScope() {
        return scope;
    }
}
