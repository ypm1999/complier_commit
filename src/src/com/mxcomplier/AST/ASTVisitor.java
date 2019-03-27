package com.mxcomplier.AST;

public interface ASTVisitor {
    public void visit(ProgramNode node);

    public void visit(FuncDefNode node);

    public void visit(ClassDefNode node);

    public void visit(VarDefNode node);

    public void visit(TypeNode node);

    public void visit(CompStmtNode node);

    public void visit(ExprStmtNode node);

    public void visit(IfStmtNode node);

    public void visit(WhileStmtNode node);

    public void visit(ForStmtNode node);

    public void visit(ContinueStmtNode node);

    public void visit(BreakStmtNode node);

    public void visit(ReturnStmtNode node);

    public void visit(BlankStmtNode node);

    public void visit(SuffixExprNode node);

    public void visit(FuncCallExprNode node);

    public void visit(ArrCallExprNode node);

    public void visit(MemberCallExprNode node);

    public void visit(PrefixExprNode node);

    public void visit(NewExprNode node);

    public void visit(BinaryExprNode node);

    public void visit(AssignExprNode node);

    public void visit(IdentExprNode node);

    public void visit(ThisExprNode node);

    public void visit(IntConstExprNode node);

    public void visit(StringConstExprNode node);

    public void visit(BoolConstExprNode node);

    public void visit(NullExprNode node);

}
