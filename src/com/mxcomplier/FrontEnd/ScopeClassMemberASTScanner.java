package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.*;
import com.mxcomplier.Error.ComplierError;
import com.mxcomplier.Scope.FuncSymbol;
import com.mxcomplier.Type.ClassType;
import com.mxcomplier.Type.IntType;
import com.mxcomplier.Type.Type;

import java.util.ArrayList;
import java.util.List;

//add class members ans all functions, add function parameters
public class ScopeClassMemberASTScanner extends ASTScanner{


    void checkMain(Location location){
        FuncSymbol main =  currentScope.getFunc("main", location);
        if (!main.getParameters().isEmpty() || main.getReturnType() != IntType.getInstance())
            throw new ComplierError("main method error");
    }


    @Override
    public void visit(ProgramNode node) {
        globalScope = currentScope = node.getScope();

        for (Node section : node.getSections()){
            if (section  instanceof FuncDefNode || section  instanceof ClassDefNode)
                section.accept(this);
        }

        checkMain(node.getLocation());
        globalScope = currentScope = null;
    }

    @Override
    public void visit(ClassDefNode node) {
        currentScope = node.getScope();

        for (FuncDefNode func : node.getFuncDefs())
            func.accept(this);
        for (VarDefNode vars : node.getMemberDefs())
            vars.accept(this);

        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(FuncDefNode node) {
        currentScope = node.getFuncBody().getScope();
        List<Type> args = new ArrayList<>();
        for (VarDefNode arg : node.getParameters()){
            arg.accept(this);
            Type type = arg.getType().getType();
            if (type instanceof ClassType)
                type = globalScope.getClass(((ClassType) type).getName(), node.getLocation()).getType();
            args.add(type);
        }
        currentScope = currentScope.getParent();

        FuncSymbol symbol = new FuncSymbol(node.getName(), node.getReturnType().getType(), node.getFuncBody().getScope(), args);
        currentScope.put(symbol, node.getLocation());
        node.getFuncBody().getScope().setParent(currentScope);
    }

    @Override
    public void visit(VarDefNode node) {
        putVar(node);
    }

}
