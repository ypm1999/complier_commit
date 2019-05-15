//package com.mxcomplier.FrontEnd;
//
//import com.mxcomplier.AST.*;
//import com.mxcomplier.Scope.FuncSymbol;
//import com.mxcomplier.Scope.Scope;
//import com.mxcomplier.Scope.Symbol;
//import com.mxcomplier.Scope.VarSymbol;
//import com.mxcomplier.Type.ClassType;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.Stack;
//
//public class OutputIrrelevantOptim extends ASTScanner {
//    private Scope globalScope;
//    private Set<Symbol> globalVariables = new HashSet<>();
//    Set<Symbol> allSymbols;
//
//
//    private Set<Symbol> collectSet;
//
//    private Stack<Symbol> assignDependenceStack = new Stack<>();
//    private Stack<Set<Symbol>> controlDependenceStack = new Stack<>();
//    private FuncSymbol currentFunc;
//    private FuncSymbol mainFunction;
//
//    public class DependenceEdge {
//        Symbol base, rely;
//        DependenceEdge (Symbol base, Symbol rely) {
//            this.base = base;
//            this.rely = rely;
//        }
//
//        @Override
//        public int hashCode() {
//            return base.hashCode() + rely.hashCode();
//        }
//        @Override
//        public boolean phyEquals(Object o) {
//            return o instanceof DependenceEdge
//                    && base == ((DependenceEdge)o).base
//                    && rely == ((DependenceEdge)o).rely;
//        }
//    }
//
//    private Set<DependenceEdge> visited = new HashSet<>();
//    private void propaOutputIrrelevant(Symbol now){
//
//    }
//
//    private void init(ProgramNode node){
//        globalScope = node.getScope();
//        for (Symbol symbol : globalScope.getIdentMap().values()){
//            if (symbol instanceof VarSymbol)
//                globalVariables.add(symbol);
//            if (symbol.getName().phyEquals("main") && symbol instanceof FuncSymbol)
//                mainFunction = (FuncSymbol) symbol;
//        }
//        currentFunc = mainFunction;
//
//        allSymbols = globalScope.getAllSymbols();
//        allSymbols.forEach(x -> x.setOutputIrrelevant(true));
//        globalScope.getFunc("print").setOutputIrrelevant(false);
//        globalScope.getFunc("println").setOutputIrrelevant(false);
//        mainFunction.setOutputIrrelevant(false);
//    }
//
//    @Override
//    public void visit(ProgramNode node) {
//        init(node);
//
//        int oldValue = -1, curValue = 0;
//        while (oldValue != curValue){
//            oldValue = curValue;
//            curValue = 0;
//            for (Node section : node.getSections())
//                section.accept(this);
//
//            for (Symbol symbol : allSymbols) {
//                propaOutputIrrelevant(symbol);
//            }
//            for (Symbol symbol : allSymbols) {
//                if (!(symbol.isOutputIrrelevant()))
//                    curValue++;
//            }
//        }
//
//        //TODO
//        for (Node section : node.getSections())
//            section.accept(this);
//        visited.clear();
//    }
//
//    @Override
//    public void visit(FuncDefNode node) {
//        currentFunc = currentScope.getFunc(node.getName(), node.getLocation());
//
//        for (VarDefNode parat : node.getParameters())
//            currentFunc.addDependence(node.getFuncBody().getScope().getVar(parat.getName()));
//
//        node.getFuncBody().accept(this);
//
//
//        currentFunc = null;
//    }
//
//    @Override
//    public void visit(ClassDefNode node) {
////        currentClass = currentScope.getClass(node.getName(), node.getLocation());
//        currentScope = node.getScope();
//
//        for (VarDefNode vars : node.getMemberDefs())
//            vars.accept(this);
//
//        for (FuncDefNode func : node.getFuncDefs())
//            func.accept(this);
//
//        currentScope = currentScope.getParent();
////        currentClass = null;
//    }
//
//
//    @Override
//    public void visit(VarDefNode node) {
//        if (node.getInitExpr() != null){
//            visit(new AssignExprNode())
//        }
//    }
//
//    @Override
//    public void visit(TypeNode node) {
//
//    }
//
//    @Override
//    public void visit(CompStmtNode node) {
//
//    }
//
//    @Override
//    public void visit(ExprStmtNode node) {
//
//    }
//
//    @Override
//    public void visit(IfStmtNode node) {
//        if (isInCollectMode()){
//            node.getJudgeExpr().accept(this);
//            if (node.getThenStmt() != null)
//                node.getThenStmt().accept(this);
//            if (node.getElseStmt() != null)
//                node.getElseStmt().accept(this);
//        }
//        else{
//            beginCollect();
//            node.getJudgeExpr().accept(this);
//            Set<Symbol> controlVars = new HashSet<>(fetchCollect());
//
//            controlDependenceStack.push(controlVars);
//            node.getJudgeExpr().accept(this);
//
//            if (node.getThenStmt() != null)
//                node.getThenStmt().accept(this);
//            if (node.getElseStmt() != null)
//                node.getElseStmt().accept(this);
//
//            controlDependenceStack.pop();
//
//            mark
//
//
//
//        }
//
//    }
//
//    @Override
//    public void visit(WhileStmtNode node) {
//
//    }
//
//    @Override
//    public void visit(ForStmtNode node) {
//
//    }
//
//    @Override
//    public void visit(ContinueStmtNode node) {
//
//    }
//
//    @Override
//    public void visit(BreakStmtNode node) {
//
//    }
//
//    @Override
//    public void visit(ReturnStmtNode node) {
//
//    }
//
//    @Override
//    public void visit(BlankStmtNode node) {
//    }
//
//    private int sideEffect = 0;
//    @Override
//    public void visit(SuffixExprNode node) {
//        node.getSubExpr().accept(this);
//        if (!isInCollectMode()){
//            sideEffect++;
//        }
//    }
//
//    @Override
//    public void visit(FuncCallExprNode node) {
//
//    }
//
//    @Override
//    public void visit(ArrCallExprNode node) {
//
//    }
//
//    @Override
//    public void visit(MemberCallExprNode node) {
//
//    }
//
//    @Override
//    public void visit(PrefixExprNode node) {
//        node.getSubExpr().accept(this);
//        if (!isInCollectMode()){
//            if (node.getPrefixOp() == PrefixExprNode.PrefixOp.PREFIX_DEC || node.getPrefixOp() == PrefixExprNode.PrefixOp.PREFIX_INC)
//                sideEffect++;
//        }
//    }
//
//    @Override
//    public void visit(NewExprNode node) {
//
//    }
//
//    @Override
//    public void visit(BinaryExprNode node) {
//
//    }
//
//    @Override
//    public void visit(AssignExprNode node) {
//
//    }
//
//    @Override
//    public void visit(IdentExprNode node) {
//
//    }
//
//    @Override
//    public void visit(ThisExprNode node) {
//
//    }
//
//    @Override
//    public void visit(IntConstExprNode node) {
//
//    }
//
//    @Override
//    public void visit(StringConstExprNode node) {
//
//    }
//
//    @Override
//    public void visit(BoolConstExprNode node) {
//
//    }
//
//    @Override
//    public void visit(NullExprNode node) {
//
//    }
//
//    private boolean isInCollectMode() {
//        return collectSet != null;
//    }
//
//    private void beginCollect() {
//        collectSet = new HashSet<>();
//    }
//
//    private Set<Symbol> fetchCollect() {
//        Set<Symbol> ret = collectSet;
//        collectSet = null;
//        return ret;
//    }
//
//
//    private void markNode(Node node, Set<Symbol> controlVars) {
//        if (controlVars.size() == 0) {
//            node.setOutputIrrelevant(false);
//        } else {
//            boolean irrelevant = true;
//            for (Symbol controlVar : controlVars) {
//                if (!controlVar.isOutputIrrelevant())
//                    irrelevant = false;
//            }
//            node.setOutputIrrelevant(irrelevant);
//        }
//    }
//}
