package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.*;
import com.mxcomplier.Ir.ProgramIR;
import com.mxcomplier.Scope.FuncSymbol;
import com.mxcomplier.Scope.Scope;
import com.mxcomplier.Scope.Symbol;
import com.mxcomplier.Scope.VarSymbol;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class OutputIrrelevantOptim extends ASTScanner {
    private Scope globalScope;
    private Set<Symbol> globalVariables = new HashSet<>();
    Set<Symbol> allSymbols;


    private Set<Symbol> collectSet;

    private Stack<Symbol> assignDependenceStack = new Stack<>();
    private Stack<Set<Symbol>> controlDependenceStack = new Stack<>();
    private FuncSymbol currentFunction;
    private FuncSymbol mainFunction;

    public class DependenceEdge {
        Symbol base, rely;
        DependenceEdge (Symbol base, Symbol rely) {
            this.base = base;
            this.rely = rely;
        }

        @Override
        public int hashCode() {
            return base.hashCode() + rely.hashCode();
        }
        @Override
        public boolean equals(Object o) {
            return o instanceof DependenceEdge
                    && base == ((DependenceEdge)o).base
                    && rely == ((DependenceEdge)o).rely;
        }
    }

    private Set<DependenceEdge> visited = new HashSet<>();
    private void propaOutputIrrelevant(Symbol now){

    }

    private void init(ProgramNode node){
        globalScope = node.getScope();
        for (Symbol symbol : globalScope.getIdentMap().values()){
            if (symbol instanceof VarSymbol)
                globalVariables.add(symbol);
            if (symbol.getName().equals("main") && symbol instanceof FuncSymbol)
                mainFunction = (FuncSymbol) symbol;
        }
        currentFunction = mainFunction;

        allSymbols = globalScope.getAllSymbols();
        allSymbols.forEach(x -> x.setOutputIrrelevant(true));
        globalScope.getFunc("print").setOutputIrrelevant(false);
        globalScope.getFunc("println").setOutputIrrelevant(false);
        mainFunction.setOutputIrrelevant(false);
    }

    @Override
    public void visit(ProgramNode node) {
        init(node);

        int oldValue = -1, curValue = 0;
        while (oldValue != curValue){
            oldValue = curValue;
            curValue = 0;
            for (Node section : node.getSections())
                section.accept(this);

            for (Symbol symbol : allSymbols) {
                propaOutputIrrelevant(symbol);
            }
            for (Symbol symbol : allSymbols) {
                if (!(symbol.isOutputIrrelevant()))
                    curValue++;
            }
        }

        //TODO
        for (Node section : node.getSections())
            section.accept(this);
        visited.clear();
    }


    @Override
    public void visit(FuncDefNode node) {

    }

    @Override
    public void visit(ClassDefNode node) {

    }

    @Override
    public void visit(VarDefNode node) {

    }

    @Override
    public void visit(TypeNode node) {

    }

    @Override
    public void visit(CompStmtNode node) {

    }

    @Override
    public void visit(ExprStmtNode node) {

    }

    @Override
    public void visit(IfStmtNode node) {

    }

    @Override
    public void visit(WhileStmtNode node) {

    }

    @Override
    public void visit(ForStmtNode node) {

    }

    @Override
    public void visit(ContinueStmtNode node) {

    }

    @Override
    public void visit(BreakStmtNode node) {

    }

    @Override
    public void visit(ReturnStmtNode node) {

    }

    @Override
    public void visit(BlankStmtNode node) {

    }

    @Override
    public void visit(SuffixExprNode node) {

    }

    @Override
    public void visit(FuncCallExprNode node) {

    }

    @Override
    public void visit(ArrCallExprNode node) {

    }

    @Override
    public void visit(MemberCallExprNode node) {

    }

    @Override
    public void visit(PrefixExprNode node) {

    }

    @Override
    public void visit(NewExprNode node) {

    }

    @Override
    public void visit(BinaryExprNode node) {

    }

    @Override
    public void visit(AssignExprNode node) {

    }

    @Override
    public void visit(IdentExprNode node) {

    }

    @Override
    public void visit(ThisExprNode node) {

    }

    @Override
    public void visit(IntConstExprNode node) {

    }

    @Override
    public void visit(StringConstExprNode node) {

    }

    @Override
    public void visit(BoolConstExprNode node) {

    }

    @Override
    public void visit(NullExprNode node) {

    }



}
