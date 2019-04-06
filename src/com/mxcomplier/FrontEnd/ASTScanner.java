package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.*;
import com.mxcomplier.Scope.ClassSymbol;
import com.mxcomplier.Scope.Scope;
import com.mxcomplier.Scope.Symbol;
import com.mxcomplier.Scope.VarSymbol;
import com.mxcomplier.Type.ArrayType;
import com.mxcomplier.Type.ClassType;
import com.mxcomplier.Type.NullType;
import com.mxcomplier.Type.Type;

public class ASTScanner implements ASTVisitor {
    Scope currentScope = null;
    Scope globalScope = null;

    Symbol getClassMember(String className, String memberName, Location location) {
        Symbol symbol = globalScope.tryGetFunc(memberName);
        if (symbol != null)
            return symbol;
        symbol = globalScope.getClass(className, location);
        return ((ClassSymbol) symbol).getScope().getSelf(memberName, location);
    }

    void putVar(VarDefNode node) {
        Type type = node.getType().getType();
        if (type instanceof ClassType)
            type = globalScope.getClass(((ClassType) type).getName(), node.getLocation()).getType();
        currentScope.put(new VarSymbol(node.getName(), type));
    }

    boolean typeAssignable(Type leftType, Type rightType) {
        return leftType.equals(rightType) ||
                ((leftType instanceof ArrayType || leftType instanceof ClassType) && rightType instanceof NullType);
    }


    @Override
    public void visit(ProgramNode node) {

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
