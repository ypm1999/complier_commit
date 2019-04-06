package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.*;
import com.mxcomplier.Error.ComplierError;
import com.mxcomplier.Scope.*;
import com.mxcomplier.Type.IntType;
import com.mxcomplier.Type.StringType;
import com.mxcomplier.Type.Type;
import com.mxcomplier.Type.VoidType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//add BuildInFunc, class define, function define and class method define
public class ScopePrepareASTScanner extends ASTScanner{

    private void addBuildInFunc(Scope scope, String name, Type returnType, List<Type> args){
        scope.put(new FuncSymbol(name, returnType, null, args));
    }

    private void prepareGlobalScope(){
        Scope stringScope = new Scope(currentScope);
        Scope arrayScope = new Scope(currentScope);

        Type intType = IntType.getInstance();
        Type voidType = VoidType.getInstance();
        Type stringType = StringType.getInstance();

        addBuildInFunc(stringScope, "length", intType, new ArrayList<>());
        addBuildInFunc(stringScope, "parseInt", intType, new ArrayList<>());
        addBuildInFunc(arrayScope, "size", intType, new ArrayList<>());
        addBuildInFunc(currentScope, "getString", stringType, new ArrayList<>());
        addBuildInFunc(currentScope, "getInt", intType, new ArrayList<>());
        addBuildInFunc(currentScope, "print", voidType, Collections.singletonList(stringType));
        addBuildInFunc(currentScope, "println", voidType, Collections.singletonList(stringType));
        addBuildInFunc(stringScope, "ord", intType, Collections.singletonList(intType));
        addBuildInFunc(currentScope, "toString", stringType, Collections.singletonList(intType));
        addBuildInFunc(stringScope, "substring", stringType, Arrays.asList(intType, intType));

//        currentScope.put(new ClassSymbol("int", new Scope(currentScope)));
//        currentScope.put(new ClassSymbol("bool", new Scope(currentScope)));
//        currentScope.put(new ClassSymbol("void", new Scope(currentScope)));
        currentScope.put(new ClassSymbol("string", stringScope));
        currentScope.put(new ClassSymbol("__array", arrayScope));
    }

    @Override
    public void visit(ProgramNode node) {
        currentScope = node.getScope();

        prepareGlobalScope();

        for (Node section : node.getSections()){
            if (section  instanceof ClassDefNode)
                section.accept(this);
        }

        currentScope = currentScope.getParent();
    }


    @Override
    public void visit(ClassDefNode node) {
        Symbol symbol = new ClassSymbol(node.getName(), node);
        currentScope.put(symbol, node.getLocation());

        node.getScope().setParent(currentScope);
        currentScope = node.getScope();

        currentScope = currentScope.getParent();
    }
}
