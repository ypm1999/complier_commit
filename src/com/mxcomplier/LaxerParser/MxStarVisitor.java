// Generated from /home/sjtu-ypm/complier/src/src/com/mxcomplier/LaxerParser/MxStar.g4 by ANTLR 4.7.2
package com.mxcomplier.LaxerParser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MxStarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface MxStarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link MxStarParser#program}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProgram(MxStarParser.ProgramContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#sections}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSections(MxStarParser.SectionsContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#functionDefinition}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionDefinition(MxStarParser.FunctionDefinitionContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#classDefinition}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitClassDefinition(MxStarParser.ClassDefinitionContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#classStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitClassStatement(MxStarParser.ClassStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#declarationList}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDeclarationList(MxStarParser.DeclarationListContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#declaration}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDeclaration(MxStarParser.DeclarationContext ctx);

    /**
     * Visit a parse tree produced by the {@code thisExpr}
     * labeled alternative in {@link MxStarParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitThisExpr(MxStarParser.ThisExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code memberCallExpr}
     * labeled alternative in {@link MxStarParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMemberCallExpr(MxStarParser.MemberCallExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code arrayCallExpr}
     * labeled alternative in {@link MxStarParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArrayCallExpr(MxStarParser.ArrayCallExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code functionCallExpr}
     * labeled alternative in {@link MxStarParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionCallExpr(MxStarParser.FunctionCallExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code subExpr}
     * labeled alternative in {@link MxStarParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSubExpr(MxStarParser.SubExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code identifierExpr}
     * labeled alternative in {@link MxStarParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdentifierExpr(MxStarParser.IdentifierExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code constantExpr}
     * labeled alternative in {@link MxStarParser#primaryExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConstantExpr(MxStarParser.ConstantExprContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#argumentExpressionList}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArgumentExpressionList(MxStarParser.ArgumentExpressionListContext ctx);

    /**
     * Visit a parse tree produced by the {@code newExpr}
     * labeled alternative in {@link MxStarParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNewExpr(MxStarParser.NewExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code prefixExpr}
     * labeled alternative in {@link MxStarParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPrefixExpr(MxStarParser.PrefixExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code primaryExpr}
     * labeled alternative in {@link MxStarParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPrimaryExpr(MxStarParser.PrimaryExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code binaryExpr}
     * labeled alternative in {@link MxStarParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBinaryExpr(MxStarParser.BinaryExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code suffixIncDec}
     * labeled alternative in {@link MxStarParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSuffixIncDec(MxStarParser.SuffixIncDecContext ctx);

    /**
     * Visit a parse tree produced by the {@code assignExpr}
     * labeled alternative in {@link MxStarParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAssignExpr(MxStarParser.AssignExprContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#newExpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNewExpression(MxStarParser.NewExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#statement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStatement(MxStarParser.StatementContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#compoundStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCompoundStatement(MxStarParser.CompoundStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#compoundStatementItem}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCompoundStatementItem(MxStarParser.CompoundStatementItemContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#declarationStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDeclarationStatement(MxStarParser.DeclarationStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#expressionStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpressionStatement(MxStarParser.ExpressionStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#selectionStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSelectionStatement(MxStarParser.SelectionStatementContext ctx);

    /**
     * Visit a parse tree produced by the {@code whileStatement}
     * labeled alternative in {@link MxStarParser#iterationStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWhileStatement(MxStarParser.WhileStatementContext ctx);

    /**
     * Visit a parse tree produced by the {@code forStatement}
     * labeled alternative in {@link MxStarParser#iterationStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitForStatement(MxStarParser.ForStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#forCondition}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitForCondition(MxStarParser.ForConditionContext ctx);

    /**
     * Visit a parse tree produced by the {@code reutrnStmt}
     * labeled alternative in {@link MxStarParser#jumpStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitReutrnStmt(MxStarParser.ReutrnStmtContext ctx);

    /**
     * Visit a parse tree produced by the {@code continueStmt}
     * labeled alternative in {@link MxStarParser#jumpStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitContinueStmt(MxStarParser.ContinueStmtContext ctx);

    /**
     * Visit a parse tree produced by the {@code breakStmt}
     * labeled alternative in {@link MxStarParser#jumpStatement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBreakStmt(MxStarParser.BreakStmtContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#identifier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdentifier(MxStarParser.IdentifierContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#baseType}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBaseType(MxStarParser.BaseTypeContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(MxStarParser.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#typeOrVoid}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeOrVoid(MxStarParser.TypeOrVoidContext ctx);

    /**
     * Visit a parse tree produced by {@link MxStarParser#bracketIdentifier}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBracketIdentifier(MxStarParser.BracketIdentifierContext ctx);

    /**
     * Visit a parse tree produced by the {@code boolConst}
     * labeled alternative in {@link MxStarParser#constant}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBoolConst(MxStarParser.BoolConstContext ctx);

    /**
     * Visit a parse tree produced by the {@code nullConst}
     * labeled alternative in {@link MxStarParser#constant}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNullConst(MxStarParser.NullConstContext ctx);

    /**
     * Visit a parse tree produced by the {@code intConst}
     * labeled alternative in {@link MxStarParser#constant}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIntConst(MxStarParser.IntConstContext ctx);

    /**
     * Visit a parse tree produced by the {@code stringConst}
     * labeled alternative in {@link MxStarParser#constant}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStringConst(MxStarParser.StringConstContext ctx);
}