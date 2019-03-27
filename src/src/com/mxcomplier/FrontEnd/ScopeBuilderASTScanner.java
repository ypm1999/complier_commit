package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.*;
import com.mxcomplier.Error.ComplierError;
import com.mxcomplier.Scope.*;
import com.mxcomplier.Type.Type.HyperType;
import com.mxcomplier.Type.*;


import java.util.Iterator;


public class ScopeBuilderASTScanner extends ASTScanner{
    private ClassSymbol currentClass = null;
    private FuncSymbol currentFunc = null;
    private boolean inLoop = false;
//    private boolean getReturn = false;

    private void checkVarInit(VarDefNode node){
        ExprNode initExpr = node.getInitExpr();
        if (initExpr != null){
            initExpr.accept(this);
            Type nodeType = node.getType().getType();
            if ((nodeType == BoolType.getInstance()) ||
                (initExpr.getType() instanceof NullType && !(nodeType instanceof ArrayType || nodeType instanceof ClassType)) ||
                !nodeType.equals(initExpr.getType()) ||
                node.isFuncArgs())
                throw new ComplierError(node.getLocation(), "Variable initialization is invalid");
        }
    }

    @Override
    public void visit(ProgramNode node) {
        globalScope = currentScope = node.getScope();

        for (Node section : node.getSections())
            section.accept(this);

        globalScope = currentScope = null;
    }

    @Override
    public void visit(FuncDefNode node) {
        currentFunc = currentScope.getFunc(node.getName(), node.getLocation());

        if (node.getReturnType().getType() instanceof ClassType)
            globalScope.getClass(((ClassType) node.getReturnType().getType()).getName(), node.getLocation());
        node.getFuncBody().accept(this);
        currentFunc = null;
    }

    @Override
    public void visit(ClassDefNode node) {
        currentClass = currentScope.getClass(node.getName(), node.getLocation());
        currentScope = node.getScope();

        for (VarDefNode vars : node.getMemberDefs())
            vars.accept(this);

        for (FuncDefNode func : node.getFuncDefs())
            func.accept(this);

        currentScope = currentScope.getParent();
        currentClass = null;
    }

    @Override
    public void visit(VarDefNode node) {
        checkVarInit(node);
        if (!node.isMemberDef())
            putVar(node);

    }

    @Override
    public void visit(TypeNode node) {
        throw new ComplierError(node.getLocation(), "view TypeNode");
    }

    @Override
    public void visit(CompStmtNode node) {
        currentScope = node.getScope();

        for (Node stmt : node.getStmtlist()){
            stmt.accept(this);
            if (currentFunc != null && stmt instanceof ReturnStmtNode)
                if (((ReturnStmtNode) stmt).getType() != currentFunc.getReturnType())
                    throw new ComplierError(stmt.getLocation(), "return type not match");
        }

        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(ExprStmtNode node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(IfStmtNode node) {
        node.getJudgeExpr().accept(this);
        if (node.getJudgeExpr().getType() != BoolType.getInstance())
            throw new ComplierError(node.getJudgeExpr().getLocation(),
                    "the judge expression in IF must get bool value");
        node.getThenStmt().accept(this);
        if (node.getElseStmt() != null)
            node.getElseStmt().accept(this);
    }

    @Override
    public void visit(WhileStmtNode node) {
        node.getJudgeExpr().accept(this);
        if (node.getJudgeExpr().getType() != BoolType.getInstance())
            throw new ComplierError(node.getJudgeExpr().getLocation(),
                    "the judge expression in WHILE must get bool value");

        boolean oldInLoop = inLoop;
        inLoop = true;
        node.getStmt().accept(this);
        inLoop = oldInLoop;
    }

    @Override
    public void visit(ForStmtNode node) {
        if (node.getExpr1() != null)
            node.getExpr1().accept(this);
        if (node.getExpr2() != null) {
            node.getExpr2().accept(this);
            if(node.getExpr2().getType() != BoolType.getInstance())
                throw new ComplierError(node.getExpr2().getLocation(),
                                    "the second expression in FOR must get bool value");
        }
        if (node.getExpr3() != null)
            node.getExpr3().accept(this);
        boolean oldInLoop = inLoop;
        inLoop = true;
        node.getStmt().accept(this);
        inLoop = oldInLoop;
    }

    @Override
    public void visit(ContinueStmtNode node) {
        if (!inLoop)
            throw new ComplierError(node.getLocation(), "continue is not in loop");
    }

    @Override
    public void visit(BreakStmtNode node) {
        if (!inLoop)
            throw new ComplierError(node.getLocation(), "break is not in loop");
    }

    @Override
    public void visit(ReturnStmtNode node) {
        if (currentFunc == null)
            throw new ComplierError(node.getLocation(), "return is not in function");
        if (node.getReturnExpr() == null)
            node.setType(VoidType.getInstance());
        else {
            node.getReturnExpr().accept(this);
            node.setType(node.getReturnExpr().getType());
        }
    }

    @Override
    public void visit(BlankStmtNode node) {
        //Do nothing
    }

    @Override
    public void visit(SuffixExprNode node) {
        node.getSubExpr().accept(this);
        if (node.getSubExpr().getType() != IntType.getInstance())
            throw new ComplierError(node.getLocation(), "invalid suffix expression");
        node.setLeftValue(node.getSubExpr().isLeftValue());
        node.setType(node.getSubExpr().getType());
    }

    @Override
    public void visit(FuncCallExprNode node) {
        ExprNode base = node.getBaseExpr();
        base.accept(this);

        if (!node.getArgumentList().isEmpty()){
            FuncSymbol func;
            if (base instanceof IdentExprNode){
                func = globalScope.getFunc(((IdentExprNode) base).getName(), base.getLocation());
            }
            else if (base instanceof MemberCallExprNode){
                Symbol tmpSymbol = getClassMember(((ClassType)((MemberCallExprNode) base).getBaseExpr().getType()).getName(),
                                        ((MemberCallExprNode) base).getMemberName(), base.getLocation());
                if (tmpSymbol instanceof FuncSymbol)
                    func = (FuncSymbol) tmpSymbol;
                else
                    throw new ComplierError(base.getLocation(),"invalid member function call");
            }
            else
                throw new ComplierError(node.getLocation(), "unknown function call");

            Iterator<Type> it = func.getParameters().iterator();
            if (func.getParameters().size() == node.getArgumentList().size())
                for (ExprNode expr : node.getArgumentList()){
                    expr.accept(this);
                    if (expr.getType() != it.next())
                        throw new ComplierError(node.getLocation(), "paratemers' type is invalid");
                }
            else
                throw new ComplierError(node.getLocation(), "paratemers number is not equal to the function define");
        }
        node.setLeftValue(false);
        node.setType(node.getBaseExpr().getType());
    }

    @Override
    public void visit(ArrCallExprNode node) {
        node.getBaseExpr().accept(this);
        node.getSubscriptExpr().accept(this);

        if (node.getSubscriptExpr().getType() != IntType.getInstance())
            throw new ComplierError(node.getLocation(), "dim of array must be int");
        if (!(node.getBaseExpr().getType() instanceof ArrayType))
            throw new ComplierError(node.getLocation(), "array call error, not an array");

        node.setLeftValue(true);
        node.setType(((ArrayType) node.getBaseExpr().getType()).getBaseType());
    }

    @Override
    public void visit(MemberCallExprNode node) {
        ExprNode baseExpr = node.getBaseExpr();
        baseExpr.accept(this);
        Symbol varOrFunc;
        if (baseExpr.getType() instanceof ClassType){
            varOrFunc = getClassMember(((ClassType) baseExpr.getType()).getName(),
                                              node.getMemberName(), node.getLocation());
        }
        else if (baseExpr.getType() instanceof StringType){
            varOrFunc = getClassMember("string", node.getMemberName(), node.getLocation());
        }
        else if (baseExpr.getType() instanceof ArrayType){
            varOrFunc = getClassMember("__array", node.getMemberName(), node.getLocation());
        }
        else
            throw new ComplierError(node.getLocation(), "only class can call member variable");

        node.setLeftValue(true);
        if (varOrFunc.getType().getHyperType() == HyperType.FUNC)
            node.setType(((FuncSymbol) varOrFunc).getReturnType());
        else
            node.setType(varOrFunc.getType());
    }

    @Override
    public void visit(PrefixExprNode node) {
        node.getSubExpr().accept(this);
        boolean valid;
        switch (node.getSubExpr().getType().getHyperType()){
            case INT:
                valid = true;
                break;
            case BOOL:
                valid = node.getPrefixOp() == PrefixExprNode.PrefixOp.NOT ||
                        node.getPrefixOp() == PrefixExprNode.PrefixOp.INV;
                break;
            default:
                valid = false;
        }
        if (!valid)
            throw new ComplierError(node.getLocation(), "invalid prefix expression");
        node.setLeftValue(node.getSubExpr().isLeftValue());
        node.setType(node.getSubExpr().getType());

    }

    @Override
    public void visit(NewExprNode node) {
        for(ExprNode dim : node.getDims()){
            dim.accept(this);
            if (!(dim.getType() instanceof IntType))
                throw new ComplierError(node.getLocation(), "dim of array must be int");
        }
        node.setLeftValue(false);
        node.setType(node.getBaseType().getType());
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);

        HyperType lhType = node.getLeftExpr().getType().getHyperType();
        HyperType rhType = node.getRightExpr().getType().getHyperType();
        if (lhType == HyperType.NULL){
            HyperType tmp = lhType;
            lhType = rhType;
            rhType = tmp;
        }

        boolean valid;
        switch (node.getOp()) {
            case PLUS:
                if (lhType == HyperType.STRING && rhType == HyperType.STRING){
                    node.setType(node.getLeftExpr().getType());
                    valid = true;
                    break;
                }
            case MUL:
            case DIV:
            case MOD:
            case MINUS:
            case LSH:
            case RSH:
                valid = lhType == HyperType.INT && rhType == HyperType.INT;
                node.setType(node.getLeftExpr().getType());
                break;
            case AND:
            case XOR:
            case OR:
                valid = (lhType == HyperType.INT && rhType == HyperType.INT) ||
                        (lhType == HyperType.BOOL && rhType == HyperType.BOOL);
                node.setType(node.getLeftExpr().getType());
                break;
            case EQUAL:
            case UNEQUAL:
                if (rhType == HyperType.NULL &&
                    (lhType == HyperType.NULL || lhType == HyperType.ARRAY || lhType == HyperType.CLASS)){
                    valid = true;
                    node.setType(BoolType.getInstance());
                    break;
                }
            case LESS:
            case LARGE:
            case LESS_EQUAL:
            case LARGE_EQUAL:
                valid = lhType == rhType;
                node.setType(BoolType.getInstance());
                break;

            case ANDAND:
            case OROR:
                valid = (lhType == HyperType.BOOL && rhType == HyperType.BOOL);
                node.setType(BoolType.getInstance());
                break;
             default:
                 valid = false;
                 node.setType(NullType.getInstance());
        }

        if (!valid)
            throw new ComplierError(node.getLocation(),
                                String.format("type error with: %s %s %s", node.getLeftExpr().getType().toString(),
                                            node.getOp().toString(), node.getLeftExpr().getType().toString()));

        node.setLeftValue(false);
    }

    @Override
    public void visit(AssignExprNode node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        Type lType = node.getLeftExpr().getType(), rType = node.getRightExpr().getType();

        if (!node.getLeftExpr().isLeftValue())
            throw new ComplierError(node.getLocation(),
                    String.format("left value(%s) is not assignable", lType.toString()));

        if (!lType.equals(rType) &&
            !(lType instanceof ArrayType && rType instanceof NullType))
            throw new ComplierError(node.getLocation(),
                    String.format("type error with %s and %s", lType.toString(), rType.toString()));


        node.setLeftValue(false);
        node.setType(lType);
    }

    @Override
    public void visit(IdentExprNode node) {
        Symbol symbol = currentScope.get(node.getName(), node.getLocation());
        if (symbol instanceof VarSymbol){
            node.setLeftValue(true);
            node.setType(symbol.getType());
        }
        else if (symbol instanceof FuncSymbol){
            node.setLeftValue(false);
            node.setType(((FuncSymbol) symbol).getReturnType());
        }
        else
            throw new ComplierError(node.getLocation(), "Identifier must be a variable or function");
    }

    @Override
    public void visit(ThisExprNode node) {
        if (currentClass == null)
            throw new ComplierError(node.getLocation(), "this pointer is not in class");
        node.setType(currentClass.getType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(IntConstExprNode node) {
        node.setType(IntType.getInstance());
        node.setLeftValue(false);
    }

    @Override
    public void visit(StringConstExprNode node) {
        node.setType(StringType.getInstance());
        node.setLeftValue(false);
    }

    @Override
    public void visit(BoolConstExprNode node) {
        node.setType(BoolType.getInstance());
        node.setLeftValue(false);
    }

    @Override
    public void visit(NullExprNode node) {
        node.setType(NullType.getInstance());
        node.setLeftValue(false);
    }
}
