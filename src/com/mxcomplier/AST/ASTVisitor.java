package com.mxcomplier.AST;

public interface ASTVisitor {
    void visit(ProgramNode node);

    void visit(FuncDefNode node);

    void visit(ClassDefNode node);

    void visit(VarDefNode node);

    void visit(TypeNode node);

    void visit(CompStmtNode node);

    void visit(ExprStmtNode node);

    void visit(IfStmtNode node);

    void visit(WhileStmtNode node);

    void visit(ForStmtNode node);

    void visit(ContinueStmtNode node);

    void visit(BreakStmtNode node);

    void visit(ReturnStmtNode node);

    void visit(BlankStmtNode node);

    void visit(SuffixExprNode node);

    void visit(FuncCallExprNode node);

    void visit(ArrCallExprNode node);

    void visit(MemberCallExprNode node);

    void visit(PrefixExprNode node);

    void visit(NewExprNode node);

    void visit(BinaryExprNode node);

    void visit(AssignExprNode node);

    void visit(IdentExprNode node);

    void visit(ThisExprNode node);

    void visit(IntConstExprNode node);

    void visit(StringConstExprNode node);

    void visit(BoolConstExprNode node);

    void visit(NullExprNode node);

}
