package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.ClassDefNode;
import com.mxcomplier.AST.Node;
import com.mxcomplier.AST.ProgramNode;
import com.mxcomplier.Scope.ClassSymbol;
import com.mxcomplier.Scope.FuncSymbol;
import com.mxcomplier.Scope.Scope;
import com.mxcomplier.Scope.Symbol;
import com.mxcomplier.Type.IntType;
import com.mxcomplier.Type.StringType;
import com.mxcomplier.Type.Type;
import com.mxcomplier.Type.VoidType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//add BuildInFunc, class define, function define and class method define
public class ScopePrepareASTScanner extends ASTScanner {

    private void addBuildInFunc(String name, Type returnType, List<Type> args, ClassSymbol belongClass) {
        currentScope.put(new FuncSymbol(name, returnType, null, args, belongClass));
    }

    private void prepareGlobalScope() {

        Scope stringScope = new Scope(currentScope);
        Scope arrayScope = new Scope(currentScope);
        ClassSymbol string = new ClassSymbol("string", stringScope);
        ClassSymbol array = new ClassSymbol("__array", arrayScope);

        Type intType = IntType.getInstance();
        Type voidType = VoidType.getInstance();
        Type stringType = StringType.getInstance();

        addBuildInFunc("length", intType, new ArrayList<>(), string);
        addBuildInFunc("parseInt", intType, new ArrayList<>(), string);
        addBuildInFunc("size", intType, new ArrayList<>(), array);
        addBuildInFunc("getString", stringType, new ArrayList<>(), null);
        addBuildInFunc("getInt", intType, new ArrayList<>(), null);
        addBuildInFunc("print", voidType, Collections.singletonList(stringType), null);
        addBuildInFunc("println", voidType, Collections.singletonList(stringType), null);
        addBuildInFunc("ord", intType, Collections.singletonList(intType), string);
        addBuildInFunc("toString", stringType, Collections.singletonList(intType), null);
        addBuildInFunc("substring", stringType, Arrays.asList(intType, intType), string);

//        currentScope.put(new ClassSymbol("int", new Scope(currentScope)));
//        currentScope.put(new ClassSymbol("bool", new Scope(currentScope)));
//        currentScope.put(new ClassSymbol("void", new Scope(currentScope)));
        currentScope.put(string);
        currentScope.put(array);
    }

    @Override
    public void visit(ProgramNode node) {
        currentScope = node.getScope();

        prepareGlobalScope();

        for (Node section : node.getSections()) {
            if (section instanceof ClassDefNode)
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
