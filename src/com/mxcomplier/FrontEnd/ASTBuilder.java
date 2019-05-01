package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.*;
import com.mxcomplier.Error.ComplierError;
import com.mxcomplier.LaxerParser.MxStarBaseVisitor;
import com.mxcomplier.LaxerParser.MxStarParser;
import com.mxcomplier.Scope.Scope;
import com.mxcomplier.Type.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends MxStarBaseVisitor<Node> {
    private Scope currentScope;

    @Override
    public Node visitProgram(MxStarParser.ProgramContext ctx) {
        List<Node> sections = new ArrayList<>();
        currentScope = new Scope(null);

        if (ctx.sections() != null)
            for (ParserRuleContext section : ctx.sections())
                sections.add(visit(section));

        return new ProgramNode(sections, currentScope, new Location(ctx.getStart()));
    }

    @Override
    public Node visitFunctionDefinition(MxStarParser.FunctionDefinitionContext ctx) {
        String name = ctx.identifier().getText();
        Node funcbody = visit(ctx.compoundStatement());
        Node returnType = null;
        List<VarDefNode> parameters = new ArrayList<>();

        if (ctx.typeOrVoid() != null)
            returnType = visit(ctx.typeOrVoid());

        if (ctx.declarationList() != null)
            for (ParserRuleContext args : ctx.declarationList().declaration()) {
                VarDefNode argsDef = (VarDefNode) visit(args);
                argsDef.setFuncArgs(true);
                parameters.add(argsDef);
            }


        return new FuncDefNode(name, (TypeNode) returnType, parameters, (CompStmtNode) funcbody, new Location(ctx.getStart()));
    }

    @Override
    public Node visitClassDefinition(MxStarParser.ClassDefinitionContext ctx) {
        String name = ctx.identifier().getText();
        List<VarDefNode> varList = new ArrayList<>();
        List<FuncDefNode> funcList = new ArrayList<>();
        Scope scope = new Scope(currentScope);
        currentScope = scope;
        if (ctx.classStatement().declarationStatement() != null)
            for (ParserRuleContext var : ctx.classStatement().declarationStatement()) {
                VarDefNode varDef = (VarDefNode) visit(var);
                varDef.setMemberDef(true);
                varList.add(varDef);
            }
        if (ctx.classStatement().functionDefinition() != null)
            for (ParserRuleContext func : ctx.classStatement().functionDefinition()) {
                funcList.add((FuncDefNode) visit(func));
            }
        currentScope = currentScope.getParent();
        return new ClassDefNode(name, varList, funcList, scope, new Location(ctx.getStart()));
    }

    @Override
    public Node visitDeclaration(MxStarParser.DeclarationContext ctx) {
        Node type, initExpr = null;
        String name = ctx.identifier().getText();
        type = visit(ctx.type());
        if (ctx.expression() != null)
            initExpr = visit(ctx.expression());

        return new VarDefNode((TypeNode) type, name, (ExprNode) initExpr, new Location(ctx.getStart()));
    }

    @Override
    public Node visitStatement(MxStarParser.StatementContext ctx) {
        if (ctx.compoundStatement() != null)
            return visit(ctx.compoundStatement());
        else if (ctx.expressionStatement() != null)
            return visit(ctx.expressionStatement());
        else if (ctx.selectionStatement() != null)
            return visit(ctx.selectionStatement());
        else if (ctx.iterationStatement() != null)
            return visit(ctx.iterationStatement());
        else if (ctx.jumpStatement() != null)
            return visit(ctx.jumpStatement());
        else throw new ComplierError(new Location(ctx.getStart()), "Unknown statement");
    }

    @Override
    public Node visitCompoundStatement(MxStarParser.CompoundStatementContext ctx) {
        List<Node> stmtlist = new ArrayList<>();
        Scope scope = new Scope(currentScope);
        if (ctx.compoundStatementItem() != null) {
            currentScope = scope;
            for (ParserRuleContext item : ctx.compoundStatementItem())
                stmtlist.add(visit(item));
            currentScope = scope.getParent();
        }
        return new CompStmtNode(stmtlist, scope, new Location(ctx.getStart()));
    }

    @Override
    public Node visitCompoundStatementItem(MxStarParser.CompoundStatementItemContext ctx) {
        if (ctx.statement() != null)
            return visit(ctx.statement());
        else if (ctx.declarationStatement() != null)
            return visit(ctx.declarationStatement());
        else throw new ComplierError(new Location(ctx.getStart()), "unKnown compound statement item");
    }

    @Override
    public Node visitWhileStatement(MxStarParser.WhileStatementContext ctx) {
        Node judgeExpr, stmt;
        judgeExpr = visit(ctx.expression());
        stmt = visit(ctx.statement());

        return new WhileStmtNode((ExprNode) judgeExpr, (StmtNode) stmt, new Location(ctx.getStart()));
    }

    @Override
    public Node visitForStatement(MxStarParser.ForStatementContext ctx) {
        ExprNode expr1 = null, expr2 = null, expr3 = null;
        StmtNode stmt = (StmtNode) visit(ctx.statement());
        if (ctx.forCondition().exp1 != null)
            expr1 = (ExprNode) visit(ctx.forCondition().exp1);
        if (ctx.forCondition().exp2 != null)
            expr2 = (ExprNode) visit(ctx.forCondition().exp2);
        if (ctx.forCondition().exp3 != null)
            expr3 = (ExprNode) visit(ctx.forCondition().exp3);
        return new ForStmtNode(expr1, expr2, expr3, stmt, new Location(ctx.getStart()));
    }

    @Override
    public Node visitDeclarationStatement(MxStarParser.DeclarationStatementContext ctx) {
        return visit(ctx.declaration());
    }

    @Override
    public Node visitExpressionStatement(MxStarParser.ExpressionStatementContext ctx) {
        if (ctx.expression() != null)
            return new ExprStmtNode((ExprNode) visit(ctx.expression()), new Location(ctx.getStart()));
        else
            return new BlankStmtNode(new Location(ctx.getStart()));
    }

    @Override
    public Node visitSelectionStatement(MxStarParser.SelectionStatementContext ctx) {
        ExprNode judgeExpr;
        StmtNode thenStmt, elseStmt = null;
        judgeExpr = (ExprNode) visit(ctx.expression());
        thenStmt = (StmtNode) visit(ctx.thenStmt);
        if (ctx.elseStmt != null)
            elseStmt = (StmtNode) visit(ctx.elseStmt);

        return new IfStmtNode(judgeExpr, thenStmt, elseStmt, new Location(ctx.getStart()));
    }

    @Override
    public Node visitBreakStmt(MxStarParser.BreakStmtContext ctx) {
        return new BreakStmtNode(new Location(ctx.getStart()));
    }

    @Override
    public Node visitContinueStmt(MxStarParser.ContinueStmtContext ctx) {
        return new ContinueStmtNode(new Location(ctx.getStart()));
    }

    @Override
    public Node visitReutrnStmt(MxStarParser.ReutrnStmtContext ctx) {
        if (ctx.expression() == null)
            return new ReturnStmtNode(null, new Location(ctx.getStart()));
        else
            return new ReturnStmtNode((ExprNode) visit(ctx.expression()), new Location(ctx.getStart()));
    }

    @Override
    public Node visitThisExpr(MxStarParser.ThisExprContext ctx) {
        return new ThisExprNode(new Location(ctx.getStart()));
    }

    @Override
    public Node visitIdentifier(MxStarParser.IdentifierContext ctx) {
        return new IdentExprNode(ctx.getText(), new Location(ctx.getStart()));
    }

    @Override
    public Node visitNullConst(MxStarParser.NullConstContext ctx) {
        return new NullExprNode(new Location(ctx.getStart()));
    }

    @Override
    public Node visitIntConst(MxStarParser.IntConstContext ctx) {
        return new IntConstExprNode(Integer.parseInt(ctx.getText()), new Location(ctx.getStart()));
    }

    @Override
    public Node visitBoolConst(MxStarParser.BoolConstContext ctx) {
        BoolConstExprNode.BoolValue value;
        switch (ctx.getText()) {
            case "true":
                value = BoolConstExprNode.BoolValue.TRUE;
                break;
            case "false":
                value = BoolConstExprNode.BoolValue.FALSE;
                break;
            default:
                throw new ComplierError(new Location(ctx.getStart()), "Invalid boolean constant");
        }

        return new BoolConstExprNode(value, new Location(ctx.getStart()));
    }

    @Override
    public Node visitConstantExpr(MxStarParser.ConstantExprContext ctx) {
        return visit(ctx.constant());
    }

    @Override
    public Node visitSubExpr(MxStarParser.SubExprContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Node visitArrayCallExpr(MxStarParser.ArrayCallExprContext ctx) {
        Node baseExpr, subscriptExpr;
        baseExpr = visit(ctx.primaryExpression());
        subscriptExpr = visit(ctx.expression());

        return new ArrCallExprNode((ExprNode) baseExpr, (ExprNode) subscriptExpr, new Location(ctx.getStart()));
    }

    @Override
    public Node visitFunctionCallExpr(MxStarParser.FunctionCallExprContext ctx) {
        Node baseExpr;
        List<ExprNode> argumentList = new ArrayList<>();
        baseExpr = visit(ctx.primaryExpression());
        if (ctx.argumentExpressionList() != null) {
            for (ParserRuleContext arug : ctx.argumentExpressionList().expression())
                argumentList.add((ExprNode) visit(arug));
        }

        return new FuncCallExprNode((ExprNode) baseExpr, argumentList, new Location(ctx.getStart()));
    }

    @Override
    public Node visitMemberCallExpr(MxStarParser.MemberCallExprContext ctx) {
        ExprNode baseExpr = (ExprNode) visit(ctx.primaryExpression());
        IdentExprNode identifier = (IdentExprNode) visit(ctx.bracketIdentifier());
        if (baseExpr instanceof ConstExprNode)
            throw new ComplierError(new Location(ctx.getStart()), "Invalid member call");

        return new MemberCallExprNode(baseExpr, identifier.getName(), new Location(ctx.getStart()));
    }

    @Override
    public Node visitBracketIdentifier(MxStarParser.BracketIdentifierContext ctx) {
        if (ctx.identifier() != null)
            return visit(ctx.identifier());
        else if (ctx.bracketIdentifier() != null)
            return visit(ctx.bracketIdentifier());
        else throw new ComplierError(new Location(ctx.getStart()), "Identifer Error");
    }

    @Override
    public Node visitStringConst(MxStarParser.StringConstContext ctx) {
        String str = ctx.getText();
        str = str.substring(1, str.length() - 1);
        return new StringConstExprNode(strTrans(str), new Location(ctx.getStart()));
    }

    private String strTrans(String str) {
        StringBuilder newStr = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\\')
                if (i + 1 == str.length())
                    throw new ComplierError("String constant error with \\");
                else {
                    i++;
                    switch (str.charAt(i)) {
                        case 'n':
                            newStr.append('\n');
                            break;
                        case '\\':
                            newStr.append('\\');
                            break;
                        case '\"':
                            newStr.append('\"');
                            break;
                        default:
                            throw new ComplierError("String constant error with \\");
                    }
                }
            else
                newStr.append(str.charAt(i));
        }
        return newStr.toString();
    }

    @Override
    public Node visitIdentifierExpr(MxStarParser.IdentifierExprContext ctx) {
        return visit(ctx.identifier());
    }

    @Override
    public Node visitSuffixIncDec(MxStarParser.SuffixIncDecContext ctx) {
        Node subExpr;
        SuffixExprNode.SuffixOp op;
        switch (ctx.op.getText()) {
            case "++":
                op = SuffixExprNode.SuffixOp.SUFFIX_INC;
                break;
            case "--":
                op = SuffixExprNode.SuffixOp.SUFFIX_DEC;
                break;
            default:
                throw new ComplierError(new Location(ctx.getStart()), "Invalid Suffix Operator");
        }
        subExpr = visit(ctx.expression());

        return new SuffixExprNode((ExprNode) subExpr, op, new Location(ctx.getStart()));
    }

    @Override
    public Node visitPrefixExpr(MxStarParser.PrefixExprContext ctx) {
        Node subExpr;


        subExpr = visit(ctx.expression());
        if (subExpr instanceof IntConstExprNode){
            IntConstExprNode intValue = (IntConstExprNode) subExpr;
            switch (ctx.op.getText()) {
                case "++":
                    return new IntConstExprNode(intValue.getValue() + 1, new Location(ctx.getStart()));
                case "--":
                    return new IntConstExprNode(intValue.getValue() - 1, new Location(ctx.getStart()));
                case "+":
                    return intValue;
                case "-":
                    return new IntConstExprNode(-intValue.getValue(), new Location(ctx.getStart()));
                case "!":
                    throw new ComplierError(new Location(ctx.getStart()), "Invalid prefix operator of Int Constant");
                case "~":
                    return new IntConstExprNode(~intValue.getValue(), new Location(ctx.getStart()));
                default:
                    throw new ComplierError(new Location(ctx.getStart()), "Invalid prefix operator");
            }
        }

        PrefixExprNode.PrefixOp op;
        switch (ctx.op.getText()) {
            case "++":
                op = PrefixExprNode.PrefixOp.PREFIX_INC;
                break;
            case "--":
                op = PrefixExprNode.PrefixOp.PREFIX_DEC;
                break;
            case "+":
                op = PrefixExprNode.PrefixOp.PLUS;
                break;
            case "-":
                op = PrefixExprNode.PrefixOp.MINUS;
                break;
            case "!":
                op = PrefixExprNode.PrefixOp.NOT;
                break;
            case "~":
                op = PrefixExprNode.PrefixOp.INV;
                break;
            default:
                throw new ComplierError(new Location(ctx.getStart()), "Invalid prefix operator");
        }
        return new PrefixExprNode((ExprNode) subExpr, op, new Location(ctx.getStart()));
    }

    @Override
    public Node visitNewExpr(MxStarParser.NewExprContext ctx) {
        return visit(ctx.newExpression());
    }

    @Override
    public Node visitBinaryExpr(MxStarParser.BinaryExprContext ctx) {
        BinaryExprNode.Op op;
        Node leftExpr = visit(ctx.exp1), rightExpr = visit(ctx.exp2);
        switch (ctx.op.getText()) {
            case "*":
                op = BinaryExprNode.Op.MUL;
                break;
            case "/":
                op = BinaryExprNode.Op.DIV;
                break;
            case "%":
                op = BinaryExprNode.Op.MOD;
                break;
            case "+":
                op = BinaryExprNode.Op.PLUS;
                break;
            case "-":
                op = BinaryExprNode.Op.MINUS;
                break;
            case "<<":
                op = BinaryExprNode.Op.LSH;
                break;
            case ">>":
                op = BinaryExprNode.Op.RSH;
                break;
            case ">":
                op = BinaryExprNode.Op.LARGE;
                break;
            case "<":
                op = BinaryExprNode.Op.LESS;
                break;
            case ">=":
                op = BinaryExprNode.Op.LARGE_EQUAL;
                break;
            case "<=":
                op = BinaryExprNode.Op.LESS_EQUAL;
                break;
            case "==":
                op = BinaryExprNode.Op.EQUAL;
                break;
            case "!=":
                op = BinaryExprNode.Op.UNEQUAL;
                break;
            case "&":
                op = BinaryExprNode.Op.AND;
                break;
            case "^":
                op = BinaryExprNode.Op.OR;
                break;
            case "|":
                op = BinaryExprNode.Op.XOR;
                break;
            case "&&":
                op = BinaryExprNode.Op.ANDAND;
                break;
            case "||":
                op = BinaryExprNode.Op.OROR;
                break;
            default:
                throw new ComplierError(new Location(ctx.getStart()), "Invalid binary operator");
        }
        return new BinaryExprNode((ExprNode) leftExpr, (ExprNode) rightExpr, op, new Location(ctx.getStart()));
    }

    @Override
    public Node visitAssignExpr(MxStarParser.AssignExprContext ctx) {
        Node leftExpr, rightExpr;
        leftExpr = visit(ctx.primaryExpression());
        rightExpr = visit(ctx.expression());
        if (leftExpr instanceof ConstExprNode)
            throw new ComplierError(new Location(ctx.getStart()), "Invalid left value of assign");

        return new AssignExprNode((ExprNode) leftExpr, (ExprNode) rightExpr, new Location(ctx.getStart()));
    }

    @Override
    public Node visitPrimaryExpr(MxStarParser.PrimaryExprContext ctx) {
        return visit(ctx.primaryExpression());
    }

    @Override
    public Node visitNewExpression(MxStarParser.NewExpressionContext ctx) {
        Node newType;
        List<ExprNode> dims = new ArrayList<>();
        int order = 0;
        newType = visit(ctx.baseType());

        if (!ctx.expression().isEmpty()) {
            Type type = ((TypeNode) newType).getType();
            for (ParserRuleContext dim : ctx.expression()) {
                dims.add((ExprNode) visit(dim));
                order++;
            }
            order = (ctx.getChildCount() - (2 + order)) / 2;
            for (int i = order; i > 0; i--)
                type = new ArrayType(type);
            newType = new TypeNode(type, newType.getLocation());
        }

        return new NewExprNode((TypeNode) newType, dims, order, new Location(ctx.getStart()));
    }

    @Override
    public Node visitBaseType(MxStarParser.BaseTypeContext ctx) {
        Type type;
        if (ctx.Int() != null) type = IntType.getInstance();
        else if (ctx.Bool() != null) type = BoolType.getInstance();
        else if (ctx.String() != null) type = StringType.getInstance();
        else if (ctx.identifier() != null) type = new ClassType(ctx.identifier().getText());
        else throw new ComplierError(new Location(ctx.getStart()), "Type Error");

        return new TypeNode(type, new Location(ctx.getStart()));
    }

    @Override
    public Node visitType(MxStarParser.TypeContext ctx) {
        if (ctx.baseType() != null)
            return visit(ctx.baseType());
        else {
            TypeNode base = (TypeNode) visit(ctx.type());
            return new TypeNode(new ArrayType(base.getType()), base.getLocation());
        }
    }

    @Override
    public Node visitTypeOrVoid(MxStarParser.TypeOrVoidContext ctx) {
        if (ctx.Void() != null)
            return new TypeNode(VoidType.getInstance(), new Location(ctx.getStart()));
        else if (ctx.type() != null)
            return visit(ctx.type());
        else throw new ComplierError(new Location(ctx.getStart()), "Type Error");
    }
}
