package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.*;
import com.mxcomplier.Config;
import com.mxcomplier.Error.IRError;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Ir.ProgramIR;
import com.mxcomplier.Scope.*;
import com.mxcomplier.Type.*;
import com.mxcomplier.backEnd.EmptyForRemover;

import java.util.*;

public class IRBuilder extends ASTScanner {
    public ProgramIR root;
    public Map<String, FuncIR> funcMap = new HashMap<>();
    private Map<ExprNode, BasicBlockIR> trueBBMap = new HashMap<>();
    private Map<ExprNode, BasicBlockIR> falseBBMap = new HashMap<>();

    private FuncIR initFunc = null;

    private FuncIR currentFunc;
    private BasicBlockIR curBB, curLoopAfter, curLoopCondition;
    private VirtualRegisterIR curThisPointor;

    public static ImmediateIR ONE = new ImmediateIR(1), ZERO = new ImmediateIR(0);
    public static ImmediateIR REGSIZE = new ImmediateIR(Config.getREGSIZE());

    private FuncIR library_malloc, library_strcmp, library_stradd;

    private RegisterIR memoryMove(AddressIR reg) {
        if (!(reg instanceof RegisterIR)) {
            VirtualRegisterIR tmp = new VirtualRegisterIR(reg.lable + "_to_reg");
            curBB.append(new MoveInstIR(tmp, reg));
            return tmp;
        } else
            return (RegisterIR) reg;
    }

    private String transName(String className, String funcName) {
        return "_" + className + '_' + funcName;
    }

    private void initBuildInFunc() {
        FuncIR library_print = new FuncIR("print", FuncIR.Type.LIBRARY);
        FuncIR library_println = new FuncIR("println", FuncIR.Type.LIBRARY);
        FuncIR library_getString = new FuncIR("getString", FuncIR.Type.LIBRARY);
        FuncIR library_getInt = new FuncIR("getInt", FuncIR.Type.LIBRARY);
        FuncIR library_toString = new FuncIR("toString", FuncIR.Type.LIBRARY);

        FuncIR library_length = new FuncIR("_string_length", FuncIR.Type.LIBRARY);
        FuncIR library_parseInt = new FuncIR("_string_parseInt", FuncIR.Type.LIBRARY);
        FuncIR library_ord = new FuncIR("_string_ord", FuncIR.Type.LIBRARY);
        FuncIR library_substring = new FuncIR("_string_substring", FuncIR.Type.LIBRARY);
        FuncIR library_size = new FuncIR("___array_size", FuncIR.Type.LIBRARY);

        library_malloc = new FuncIR("malloc", FuncIR.Type.LIBRARY);
        library_stradd = new FuncIR("__stradd", FuncIR.Type.LIBRARY);
        library_strcmp = new FuncIR("__strcmp", FuncIR.Type.LIBRARY);
        initFunc = new FuncIR("__init", FuncIR.Type.EXTRA);

        funcMap.put(library_print.getName(), library_print);
        funcMap.put(library_println.getName(), library_println);
        funcMap.put(library_getString.getName(), library_getString);
        funcMap.put(library_getInt.getName(), library_getInt);
        funcMap.put(library_toString.getName(), library_toString);

        funcMap.put(library_length.getName(), library_length);
        funcMap.put(library_parseInt.getName(), library_parseInt);
        funcMap.put(library_ord.getName(), library_ord);
        funcMap.put(library_substring.getName(), library_substring);
        funcMap.put(library_size.getName(), library_size);

        funcMap.put(library_malloc.getName(), library_malloc);
        funcMap.put(library_stradd.getName(), library_stradd);
        funcMap.put(library_strcmp.getName(), library_strcmp);
        funcMap.put(initFunc.getName(), initFunc);
    }

    public IRBuilder() {
        initBuildInFunc();
    }

    private void addVarInitInst(AddressIR dest, ExprNode initExpr) {
        if (initExpr.getType() instanceof BoolType) {
            boolAssign(dest, initExpr);
        } else {
            initExpr.accept(this);
            curBB.append(new MoveInstIR(dest, initExpr.resultReg));
        }
    }

    private void initFunc(FuncDefNode node, String className) {
        FuncIR func = new FuncIR(className + node.getName());
        if (node.getReturnType() == null || !(node.getReturnType().getType() instanceof VoidType))
            func.returnValue = new VirtualRegisterIR("returnValue_of_" + func.getName());
        funcMap.put(func.getName(), func);
        node.setFuncIR(func);
        root.getFuncs().add(func);
    }

    @Override
    public void visit(ProgramNode node) {
        globalScope = currentScope = node.getScope();
        root = new ProgramIR();

        for (Node section : node.getSections())
            if (section instanceof FuncDefNode) {
                initFunc((FuncDefNode) section, "");
            } else if (section instanceof ClassDefNode) {
                for (FuncDefNode func : ((ClassDefNode) section).getFuncDefs())
                    initFunc(func, '_' + ((ClassDefNode) section).getName() + '_');
            }

        curBB = initFunc.entryBB = new BasicBlockIR(initFunc, "initFuncEntry");
        currentFunc = initFunc;
        boolean useinit = false;
        for (Node section : node.getSections())
            if (section instanceof VarDefNode) {
                VarSymbol var = currentScope.getVar(((VarDefNode) section).getName());
                var.vReg = new VirtualRegisterIR(((VarDefNode) section).getName());
                StaticDataIR staticData = new StaticDataIR();
                staticData.lable = var.getName();
                root.getStaticData().add(staticData);
                var.vReg.memory = new MemoryIR(staticData);
                if (((VarDefNode) section).getInitExpr() != null) {
                    addVarInitInst(var.vReg, ((VarDefNode) section).getInitExpr());
                    curBB.append(new MoveInstIR(var.vReg.memory, var.vReg));
                    currentFunc.usedGlobalVar.add(var.vReg);
                    useinit = true;
                }
            }
        initFunc.leaveBB = new BasicBlockIR(initFunc, "leave_initFunc");
        curBB.append(new JumpInstIR(initFunc.leaveBB));
        initFunc.leaveBB.append(new ReturnInstIR());
        if (!(curBB.getTail().prev instanceof BranchInstIR))
            curBB.append(new ReturnInstIR(null));
        currentFunc = null;

        for (Node section : node.getSections())
            if (!(section instanceof VarDefNode))
                section.accept(this);

        if (useinit) {
            root.getFuncs().add(initFunc);
            initFunc.caller.add(funcMap.get("main"));
            funcMap.get("main").callee.add(initFunc);
            funcMap.get("main").entryBB.prepend(new CallInstIR(initFunc, new ArrayList<>(), null));
        }

        globalScope = currentScope = currentScope.getParent();

        initFuncGlobalVar(root.getFuncs());
    }

    private void showGlobal() {
        for (FuncIR func : root.getFuncs()) {
            System.err.println(func.getName() + "::");
            StringBuilder str = new StringBuilder("used        :");
            for (VirtualRegisterIR vreg : func.usedGlobalVar)
                str.append(" ").append(vreg.lable);
            System.err.println(str);

            str = new StringBuilder("defined     :");
            for (VirtualRegisterIR vreg : func.definedGlobalVar)
                str.append(" ").append(vreg.lable);
            System.err.println(str);


            str = new StringBuilder("selfUsed    :");
            for (VirtualRegisterIR vreg : func.selfUsedGlobalVar)
                str.append(" ").append(vreg.lable);
            System.err.println(str);

            str = new StringBuilder("selfDefined :");
            for (VirtualRegisterIR vreg : func.selfDefinedGlobalVar)
                str.append(" ").append(vreg.lable);
            System.err.println(str);
            System.err.println("--------------------------------------------------");
        }
    }

    private void initFuncGlobalVar(List<FuncIR> funcs) {
        for (FuncIR func : funcs)
            func.initGlobalDefined();
        boolean change = true;
        while (change) {
            change = false;
            for (FuncIR func : funcs) {
                int oldUsedSize = func.usedGlobalVar.size();
                int oldDefinedSize = func.definedGlobalVar.size();
                for (FuncIR nextFunc : func.callee) {
                    func.usedGlobalVar.addAll(nextFunc.usedGlobalVar);
                    func.definedGlobalVar.addAll(nextFunc.definedGlobalVar);
                }
                if (oldUsedSize != func.usedGlobalVar.size() || oldDefinedSize != func.definedGlobalVar.size())
                    change = true;
            }
        }
    }

    @Override
    public void visit(FuncDefNode node) {

        currentFunc = node.getFuncIR();
        curBB = currentFunc.entryBB = new BasicBlockIR(currentFunc, "entry_" + currentFunc.getName());
        Scope funcScope = node.getFuncBody().getScope();
        List<VirtualRegisterIR> args = currentFunc.getParameters();
        ClassSymbol belongClass = funcScope.getFunc(node.getName()).getBelongClass();

        if (belongClass != null) {
            curThisPointor = new VirtualRegisterIR("this_of_" + belongClass.getName());

            args.add(curThisPointor);
        }

        for (VarDefNode arg : node.getParameters()) {
            VarSymbol var = funcScope.getVar(arg.getName());
            var.vReg = new VirtualRegisterIR(node.getName() + "_arg_" + arg.getName());
            args.add(var.vReg);
        }


        node.getFuncBody().accept(this);
        currentFunc.leaveBB = new BasicBlockIR(currentFunc, "leave_" + currentFunc.getName());

        for (BasicBlockIR bb : currentFunc.getBBList()) {
            if (bb == currentFunc.leaveBB)
                continue;
            boolean ret = false;
            if (bb.getTail().prev instanceof ReturnInstIR) {
                bb.getTail().prev.remove();
                ret = true;
            }
            if (!(bb.getTail().prev instanceof BranchInstIR)) {
                if (!ret) {
                    if (node.getReturnType() == null)
                        bb.append(new MoveInstIR(currentFunc.returnValue, curThisPointor));
                    else {
                        if (!(node.getReturnType().getType() instanceof VoidType))
                            bb.append(new MoveInstIR(currentFunc.returnValue, ZERO));
                    }
                }
                bb.append(new JumpInstIR(currentFunc.leaveBB));
            }
        }
        if (node.getReturnType() != null && node.getReturnType().getType() instanceof VoidType)
            currentFunc.leaveBB.append(new ReturnInstIR());
        else
            currentFunc.leaveBB.append(new ReturnInstIR(currentFunc.returnValue));

        currentFunc = null;
        curThisPointor = null;
    }

    @Override
    public void visit(ClassDefNode node) {
        currentScope = node.getScope();
        for (VarDefNode var : node.getMemberDefs())
            var.accept(this);
        for (FuncDefNode func : node.getFuncDefs()) {
            func.setName(func.getName());
            func.accept(this);
        }
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(VarDefNode node) {
//        System.out.println(node.getLocation().toString());
        VarSymbol var = currentScope.getVar(node.getName());
        var.vReg = new VirtualRegisterIR(node.getName());
        if (node.getInitExpr() != null)
            addVarInitInst(var.vReg, node.getInitExpr());
    }

    @Override
    public void visit(TypeNode node) {
        assert false;
    }

    @Override
    public void visit(CompStmtNode node) {
        currentScope = node.getScope();
        for (Node stmt : node.getStmtlist())
            stmt.accept(this);
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(ExprStmtNode node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(IfStmtNode node) {
        BasicBlockIR thenBB = new BasicBlockIR(currentFunc, "Ifthen");
        BasicBlockIR afterBB = new BasicBlockIR(currentFunc, "Ifafter");
        trueBBMap.put(node.getJudgeExpr(), thenBB);
        if (node.getElseStmt() != null) {
            BasicBlockIR elseBB = new BasicBlockIR(currentFunc, "Ifelse");
            falseBBMap.put(node.getJudgeExpr(), elseBB);
            node.getJudgeExpr().accept(this);

            curBB = elseBB;
            node.getElseStmt().accept(this);
            if (!(curBB.getTail().prev instanceof BranchInstIR))
                curBB.append(new JumpInstIR(afterBB));
        } else {
            falseBBMap.put(node.getJudgeExpr(), afterBB);
            node.getJudgeExpr().accept(this);
        }

        curBB = thenBB;
        node.getThenStmt().accept(this);
        if (!(curBB.getTail().prev instanceof BranchInstIR))
            curBB.append(new JumpInstIR(afterBB));
        curBB = afterBB;
    }

    @Override
    public void visit(WhileStmtNode node) {
        BasicBlockIR condBB = new BasicBlockIR(currentFunc, "whileCondition");
        BasicBlockIR bodyBB = new BasicBlockIR(currentFunc, "whileBody");
        BasicBlockIR afterBB = new BasicBlockIR(currentFunc, "whileAfter");
        curBB.append(new JumpInstIR(condBB));

        trueBBMap.put(node.getJudgeExpr(), bodyBB);
        falseBBMap.put(node.getJudgeExpr(), afterBB);
        curBB = condBB;
        node.getJudgeExpr().accept(this);

        curBB = bodyBB;
        BasicBlockIR oldLoop = curLoopAfter, oldLoopCondition = curLoopCondition;
        curLoopAfter = afterBB;
        curLoopCondition = condBB;
        node.getStmt().accept(this);
        curLoopAfter = oldLoop;
        curLoopCondition = oldLoopCondition;
        if (!(curBB.getTail().prev instanceof BranchInstIR))
            curBB.append(new JumpInstIR(condBB));

        curBB = afterBB;
    }

    @Override
    public void visit(ForStmtNode node) {
        BasicBlockIR condBB = new BasicBlockIR(currentFunc, "forCondition");
        BasicBlockIR expr3BB = new BasicBlockIR(currentFunc, "forexpr3");
        BasicBlockIR bodyBB = new BasicBlockIR(currentFunc, "forBody");
        BasicBlockIR afterBB = new BasicBlockIR(currentFunc, "forAfter");
        currentFunc.forSet.add(new EmptyForRemover.ForBBs(bodyBB, condBB, expr3BB, afterBB));
        if (node.getExpr1() != null)
            node.getExpr1().accept(this);
        curBB.append(new JumpInstIR(condBB));

        if (node.getExpr2() == null) {
            if (node.getExpr3() != null) {
                curBB = expr3BB;
                node.getExpr3().accept(this);
            }
            expr3BB.append(new JumpInstIR(condBB));
            condBB.append(new JumpInstIR(bodyBB));
        } else {
            if (node.getExpr3() != null) {
                curBB = expr3BB;
                node.getExpr3().accept(this);
            }
            expr3BB.append(new JumpInstIR(condBB));
            trueBBMap.put(node.getExpr2(), bodyBB);
            falseBBMap.put(node.getExpr2(), afterBB);
            curBB = condBB;
            node.getExpr2().accept(this);
        }

        curBB = bodyBB;
        BasicBlockIR oldLoop = curLoopAfter, oldLoopCondition = curLoopCondition;
        curLoopAfter = afterBB;
        curLoopCondition = expr3BB;
        node.getStmt().accept(this);
        curLoopAfter = oldLoop;
        curLoopCondition = oldLoopCondition;
        if (!(curBB.getTail().prev instanceof BranchInstIR))
            curBB.append(new JumpInstIR(expr3BB));

        curBB = afterBB;
    }

    @Override
    public void visit(ContinueStmtNode node) {
        curBB.append(new JumpInstIR(curLoopCondition));
    }

    @Override
    public void visit(BreakStmtNode node) {
        curBB.append(new JumpInstIR(curLoopAfter));
    }

    @Override
    public void visit(ReturnStmtNode node) {
        if (node.getReturnExpr() != null) {
            if (node.getReturnExpr().getType() instanceof BoolType) {
                boolAssign(currentFunc.returnValue, node.getReturnExpr());
            } else {
                node.getReturnExpr().accept(this);
                curBB.append(new MoveInstIR(currentFunc.returnValue, node.getReturnExpr().resultReg));
            }
            curBB.append(new ReturnInstIR(currentFunc.returnValue));
        } else
            curBB.append(new ReturnInstIR());
    }

    @Override
    public void visit(BlankStmtNode node) {
        //nothing
    }

    @Override
    public void visit(SuffixExprNode node) {
        UnaryInstIR.Op op = (node.getSuffixOp() == SuffixExprNode.SuffixOp.SUFFIX_DEC) ?
                UnaryInstIR.Op.DEC : UnaryInstIR.Op.INC;
        node.getSubExpr().accept(this);
        node.resultReg = new VirtualRegisterIR("");
        curBB.append(new MoveInstIR(node.resultReg, node.getSubExpr().resultReg));
        curBB.append(new UnaryInstIR(op, node.getSubExpr().resultReg));
    }

    @Override
    public void visit(FuncCallExprNode node) {
        VirtualRegisterIR returnValue = null;
        if (!(node.getType() instanceof VoidType))
            returnValue = new VirtualRegisterIR(String.format("returnValue_of_%s", node.getFuncName()));
        List<OperandIR> args = new ArrayList<>();

        ExprNode base = node.getBaseExpr();
        FuncSymbol func = null;
        String funcName;
        if (base instanceof IdentExprNode) {
            func = currentScope.getFunc(((IdentExprNode) base).getName(), base.getLocation());
            funcName = func.getName();
            if (func.getBelongClass() != null)
                funcName = transName(func.getBelongClass().getName(), funcName);
        } else {
            String name = null;
            Type type = ((MemberCallExprNode) base).getBaseExpr().getType();
            if (type instanceof ClassType)
                name = ((ClassType) type).getName();
            else if (type instanceof StringType)
                name = "string";
            else if (type instanceof ArrayType)
                name = "__array";

            Symbol tmpSymbol = getClassMember(name, ((MemberCallExprNode) base).getMemberName(), base.getLocation());
            if (tmpSymbol instanceof FuncSymbol)
                func = (FuncSymbol) tmpSymbol;
            funcName = transName(name, func.getName());
        }


        if (func.getBelongClass() != null) {
            if (node.getBaseExpr() instanceof MemberCallExprNode) {
                node.getBaseExpr().accept(this);
                args.add(node.getBaseExpr().resultReg);
            } else
                args.add(curThisPointor);
        }

        for (ExprNode arg : node.getArgumentList()) {
            if (arg.getType() instanceof BoolType) {
                BasicBlockIR trueBB = new BasicBlockIR(currentFunc, "bool_args_trueBB");
                BasicBlockIR falseBB = new BasicBlockIR(currentFunc, "bool_args_falseBB");
                BasicBlockIR afterBB = new BasicBlockIR(currentFunc, "bool_args_afterBB");
                VirtualRegisterIR res = new VirtualRegisterIR("bool_args");
                trueBB.append(new MoveInstIR(res, ONE));
                trueBB.append(new JumpInstIR(afterBB));
                falseBB.append(new MoveInstIR(res, ZERO));
                falseBB.append(new JumpInstIR(afterBB));
                trueBBMap.put(arg, trueBB);
                falseBBMap.put(arg, falseBB);
                arg.accept(this);
                args.add(res);
                curBB = afterBB;
            } else {
                arg.accept(this);
                args.add(arg.resultReg);
            }
        }

        doFuncCall(funcMap.get(funcName), args, returnValue);

        if (trueBBMap.containsKey(node)) {
            curBB.append(new CJumpInstIR(CJumpInstIR.Op.E, returnValue, ONE,
                    trueBBMap.get(node), falseBBMap.get(node)));
        } else
            node.resultReg = returnValue;
    }

    private void doFuncCall(FuncIR func, List<OperandIR> args, VirtualRegisterIR returnValue) {
        currentFunc.callee.add(func);
        func.caller.add(currentFunc);
        curBB.append(new CallInstIR(func, args, returnValue));
    }

    @Override
    public void visit(ArrCallExprNode node) {
        node.getSubscriptExpr().accept(this);
        node.getBaseExpr().accept(this);
        RegisterIR offset = memoryMove(node.getSubscriptExpr().resultReg);
        VirtualRegisterIR base = (VirtualRegisterIR) memoryMove(node.getBaseExpr().resultReg);
        MemoryIR memory;
        if (offset instanceof ImmediateIR)
            memory = new MemoryIR(base, (int) ((ImmediateIR) offset).getValue() * Config.getREGSIZE());
        else {
            memory = new MemoryIR(base, (VirtualRegisterIR) offset);
            memory.setScale(Config.getREGSIZE());
        }
        memory.lable = "arrCall";
        if (trueBBMap.containsKey(node)) {
            curBB.append(new CJumpInstIR(CJumpInstIR.Op.E, memory, ONE,
                    trueBBMap.get(node), falseBBMap.get(node)));
        } else
            node.resultReg = memory;
    }

    @Override
    public void visit(MemberCallExprNode node) {
        node.getBaseExpr().accept(this);
        AddressIR baseExpr = node.getBaseExpr().resultReg;
        AddressIR res = null;
        if (node.getBaseExpr().getType() instanceof ArrayType || node.getBaseExpr().getType() instanceof StringType) {
            res = baseExpr;
        } else if (node.getBaseExpr().getType() instanceof ClassType) {
            String className = ((ClassType) node.getBaseExpr().getType()).getName();
            ClassSymbol classSymbol = globalScope.getClass(className);
            if (classSymbol.getScope().tryGetFunc(node.getMemberName()) != null) {
                res = baseExpr;
            } else {
                res = new MemoryIR((VirtualRegisterIR) memoryMove(baseExpr), classSymbol.getVarOffset(node.getMemberName()));
                res.lable = "memCall";
            }
        } else assert false;
        if (trueBBMap.containsKey(node)) {
            curBB.append(new CJumpInstIR(CJumpInstIR.Op.E, res, ONE,
                    trueBBMap.get(node), falseBBMap.get(node)));
        } else
            node.resultReg = res;
    }

    @Override
    public void visit(PrefixExprNode node) {
        UnaryInstIR.Op op = UnaryInstIR.Op.ERROR;
        switch (node.getPrefixOp()) {
            case PREFIX_INC:
                op = UnaryInstIR.Op.INC;
                break;
            case PREFIX_DEC:
                op = UnaryInstIR.Op.DEC;
                break;
            case PLUS:
                op = UnaryInstIR.Op.NULL;
                break;
            case MINUS:
                op = UnaryInstIR.Op.NEG;
                break;
            case INV:
                op = UnaryInstIR.Op.INV;
                break;
            case NOT: {
                BasicBlockIR trueBB = trueBBMap.get(node);
                BasicBlockIR falseBB = falseBBMap.get(node);
                trueBBMap.put(node.getSubExpr(), falseBB);
                falseBBMap.put(node.getSubExpr(), trueBB);
                node.getSubExpr().accept(this);
                return;
            }
            default:
                assert false;
        }
        node.getSubExpr().accept(this);
        if (op == UnaryInstIR.Op.NEG || op == UnaryInstIR.Op.INV) {
            VirtualRegisterIR res = new VirtualRegisterIR("Unary_temp");
            curBB.append(new MoveInstIR(res, node.getSubExpr().resultReg));
            node.resultReg = res;
        } else
            node.resultReg = node.getSubExpr().resultReg;
        curBB.append(new UnaryInstIR(op, node.resultReg));
    }

    private VirtualRegisterIR allocaClass(String name) {
        VirtualRegisterIR res = new VirtualRegisterIR("new_Class");
        if (name.equals("string")) {
            doFuncCall(library_malloc,
                    Collections.singletonList(new ImmediateIR(Config.getREGSIZE() * 2)), res);
            curBB.append(new MoveInstIR(res, ZERO));
            curBB.append(new BinaryInstIR(BinaryInstIR.Op.ADD, res, REGSIZE));
            curBB.append(new MoveInstIR(res, ZERO));
            curBB.append(new BinaryInstIR(BinaryInstIR.Op.SUB, res, REGSIZE));
        } else {
            ClassSymbol symbol = globalScope.getClass(name);
            doFuncCall(library_malloc, Collections.singletonList(new ImmediateIR(symbol.getSize())), res);
            FuncIR constructor = funcMap.getOrDefault('_' + name + '_' + name, null);
            if (constructor != null) {
                VirtualRegisterIR tmp = new VirtualRegisterIR("tmp");
                curBB.append(new MoveInstIR(tmp, res));
                doFuncCall(constructor, Collections.singletonList(tmp), res);
            }
        }
        return res;
    }

    private VirtualRegisterIR allocaArray(int order, List<RegisterIR> dims) {
        assert (order > 0 && !dims.isEmpty());
        VirtualRegisterIR res = new VirtualRegisterIR("arrayNew");
        RegisterIR dim = dims.get(0);
        dims.remove(0);

        VirtualRegisterIR size = new VirtualRegisterIR("new_size");
        curBB.append(new MoveInstIR(size, dim));
        curBB.append(new UnaryInstIR(UnaryInstIR.Op.INC, size));
        curBB.append(new BinaryInstIR(BinaryInstIR.Op.SHL, size, new ImmediateIR(3)));
        doFuncCall(library_malloc, Collections.singletonList(size), res);
        curBB.append(new MoveInstIR(new MemoryIR(res), dim));
        curBB.append(new BinaryInstIR(BinaryInstIR.Op.ADD, res, REGSIZE));
        if (dims.size() == 0)
            return res;

        BasicBlockIR condBB = new BasicBlockIR(currentFunc, "newWhileCondition");
        BasicBlockIR bodyBB = new BasicBlockIR(currentFunc, "newWhileBody");
        BasicBlockIR afterBB = new BasicBlockIR(currentFunc, "newWhileAfter");

        VirtualRegisterIR cnt = new VirtualRegisterIR("new_cnt");
        curBB.append(new MoveInstIR(size, dim));
        curBB.append(new MoveInstIR(cnt, ZERO));
        curBB.append(new JumpInstIR(condBB));
        condBB.append(new CJumpInstIR(CJumpInstIR.Op.E, cnt, size, afterBB, bodyBB));
        curBB = bodyBB;
        VirtualRegisterIR subArray = allocaArray(order - 1, dims);
        curBB.append(new MoveInstIR(new MemoryIR(res, cnt, 8), subArray));

        curBB.append(new UnaryInstIR(UnaryInstIR.Op.INC, cnt));
        curBB.append(new JumpInstIR(condBB));
        curBB = afterBB;
        return res;
    }

    @Override
    public void visit(NewExprNode node) {
        if (node.getDims().isEmpty()) {
            if (node.getBaseType().getType() instanceof ClassType) {
                node.resultReg = allocaClass(((ClassType) node.getBaseType().getType()).getName());
            } else if (node.getBaseType().getType() instanceof StringType)
                node.resultReg = allocaClass("string");
            else
                throw new IRError("new " + node.getBaseType().getType() + " is invalid");
        } else {
            List<RegisterIR> dims = new ArrayList<>();
            for (ExprNode dim : node.getDims()) {
                dim.accept(this);
                dims.add(memoryMove(dim.resultReg));
            }
            node.resultReg = allocaArray(node.getOrder(), dims);
        }
    }

    private void doLogicBinaryExpr(BinaryExprNode node, ExprNode lhs, ExprNode rhs) {
        BasicBlockIR nextLogicBB = new BasicBlockIR(currentFunc, "logicNext");

        if (node.getOp() == BinaryExprNode.Op.ANDAND) {
            trueBBMap.put(lhs, nextLogicBB);
            falseBBMap.put(lhs, falseBBMap.get(node));
        } else {
            trueBBMap.put(lhs, trueBBMap.get(node));
            falseBBMap.put(lhs, nextLogicBB);
        }
        lhs.accept(this);

        curBB = nextLogicBB;
        trueBBMap.put(rhs, trueBBMap.get(node));
        falseBBMap.put(rhs, falseBBMap.get(node));
        rhs.accept(this);
    }

    private void doRelationBinaryExpr(BinaryExprNode node, ExprNode lhs, ExprNode rhs) {
        CJumpInstIR.Op op = CJumpInstIR.Op.ERROR;
        switch (node.getOp()) {
            case LESS:
                op = CJumpInstIR.Op.L;
                break;
            case LARGE:
                op = CJumpInstIR.Op.G;
                break;
            case LESS_EQUAL:
                op = CJumpInstIR.Op.LE;
                break;
            case LARGE_EQUAL:
                op = CJumpInstIR.Op.GE;
                break;
            case EQUAL:
                op = CJumpInstIR.Op.E;
                break;
            case UNEQUAL:
                op = CJumpInstIR.Op.NE;
                break;
            default:
                assert false;
        }
        lhs.accept(this);
        rhs.accept(this);
        if (node.getLeftExpr().getType() instanceof StringType) {
            VirtualRegisterIR res = new VirtualRegisterIR("strcmp_returnValue");
            doFuncCall(library_strcmp, Arrays.asList(lhs.resultReg, rhs.resultReg), res);
            curBB.append(new CJumpInstIR(op, res, ZERO, trueBBMap.get(node), falseBBMap.get(node)));
        } else {
            curBB.append(new CJumpInstIR(op, lhs.resultReg, rhs.resultReg,
                    trueBBMap.get(node), falseBBMap.get(node)));
        }
    }

    private void doArithmeticBinaryExpr(BinaryExprNode node, ExprNode lhs, ExprNode rhs) {

        BinaryInstIR.Op op = BinaryInstIR.Op.ERROR;
        switch (node.getOp()) {
            case MUL:
                op = BinaryInstIR.Op.MUL;
                break;
            case DIV:
                op = BinaryInstIR.Op.DIV;
                break;
            case MOD:
                op = BinaryInstIR.Op.MOD;
                break;
            case PLUS:
                op = BinaryInstIR.Op.ADD;
                break;
            case MINUS:
                op = BinaryInstIR.Op.SUB;
                break;
            case LSH:
                op = BinaryInstIR.Op.SHL;
                break;
            case RSH:
                op = BinaryInstIR.Op.SHR;
                break;
            case AND:
                op = BinaryInstIR.Op.AND;
                break;
            case XOR:
                op = BinaryInstIR.Op.XOR;
                break;
            case OR:
                op = BinaryInstIR.Op.OR;
                break;
            default:
                assert false;
        }
        lhs.accept(this);
        rhs.accept(this);
        if (lhs.resultReg instanceof ImmediateIR && rhs.resultReg instanceof ImmediateIR) {
            long res = 0;
            long lvalue = ((ImmediateIR) lhs.resultReg).getValue();
            long rvalue = ((ImmediateIR) rhs.resultReg).getValue();
            switch (node.getOp()) {
                case MUL:
                    res = lvalue * rvalue;
                    break;
                case DIV:
                    res = lvalue / rvalue;
                    break;
                case MOD:
                    res = lvalue % rvalue;
                    break;
                case PLUS:
                    res = lvalue + rvalue;
                    break;
                case MINUS:
                    res = lvalue - rvalue;
                    break;
                case LSH:
                    res = lvalue << rvalue;
                    break;
                case RSH:
                    res = lvalue >> rvalue;
                    break;
                case AND:
                    res = lvalue & rvalue;
                    break;
                case XOR:
                    res = lvalue ^ rvalue;
                    break;
                case OR:
                    res = lvalue | rvalue;
                    break;
                default:
                    assert false;
            }
            node.resultReg = new ImmediateIR(res);
            return;
        }
        VirtualRegisterIR res = new VirtualRegisterIR("airthmeticBinary");
        res.tempVar = true;
        if (op == BinaryInstIR.Op.ADD && lhs.getType() instanceof StringType) {
            doFuncCall(library_stradd, Arrays.asList(lhs.resultReg, rhs.resultReg), res);
        } else {
            if (lhs.resultReg instanceof VirtualRegisterIR && ((VirtualRegisterIR) lhs.resultReg).tempVar)
                res = (VirtualRegisterIR) lhs.resultReg;
            else
                curBB.append(new MoveInstIR(res, lhs.resultReg));
            boolean finished = false;
            if (rhs.resultReg instanceof ImmediateIR){
                long value = ((ImmediateIR) rhs.resultReg).getValue();
                if (value == 1){
                    finished = true;
                    if (op == BinaryInstIR.Op.MOD)
                        curBB.prepend(new MoveInstIR(res, ZERO));
                    else if (op == BinaryInstIR.Op.ADD)
                        curBB.append(new UnaryInstIR(UnaryInstIR.Op.INC, res));
                    else if (op == BinaryInstIR.Op.SUB)
                        curBB.append(new UnaryInstIR(UnaryInstIR.Op.DEC, res));
                    else
                        if (!(op == BinaryInstIR.Op.MUL || op == BinaryInstIR.Op.DIV))
                            finished = false;
                }
                else if ((value & (value - 1)) == 0){
                    int K = 0;
                    while(value > 1){
                        K++;
                        value >>= 1;
                    }
                    finished = true;
                    if (op == BinaryInstIR.Op.MOD) {
                        curBB.append(new BinaryInstIR(BinaryInstIR.Op.AND, res,
                                new ImmediateIR(((ImmediateIR) rhs.resultReg).getValue() - 1)));
                    }
                    else
                        if (op == BinaryInstIR.Op.DIV)
                        curBB.append(new BinaryInstIR(BinaryInstIR.Op.SHR, res, new ImmediateIR(K)));
                    else if (op == BinaryInstIR.Op.MUL)
                        curBB.append(new BinaryInstIR(BinaryInstIR.Op.SHL, res, new ImmediateIR(K)));
                    else
                        finished = false;

                }
            }
            if (!finished && op == BinaryInstIR.Op.ADD && lhs.resultReg == rhs.resultReg){
                finished = true;
                curBB.append(new BinaryInstIR(BinaryInstIR.Op.SHL, res, ONE));
            }
            if (!finished)
                curBB.append(new BinaryInstIR(op, res, rhs.resultReg));
        }
        node.resultReg = res;
    }

    @Override
    public void visit(BinaryExprNode node) {
        ExprNode lhs = node.getLeftExpr(), rhs = node.getRightExpr();
        switch (node.getOp()) {
            case MUL:
            case DIV:
            case MOD:
            case PLUS:
            case MINUS:
            case LSH:
            case RSH:
            case AND:
            case XOR:
            case OR:
                doArithmeticBinaryExpr(node, lhs, rhs);
                break;
            case LESS:
            case LARGE:
            case LESS_EQUAL:
            case LARGE_EQUAL:
            case EQUAL:
            case UNEQUAL:
                doRelationBinaryExpr(node, lhs, rhs);
                break;
            case ANDAND:
            case OROR:
                doLogicBinaryExpr(node, lhs, rhs);
                break;
        }


    }

    //lhs accepted, rsh haven't accepted
    private void boolAssign(AddressIR dest, ExprNode rhs) {
        BasicBlockIR trueBB = new BasicBlockIR(currentFunc, "assignTrue");
        BasicBlockIR falseBB = new BasicBlockIR(currentFunc, "assignFalse");
        BasicBlockIR afterBB = new BasicBlockIR(currentFunc, "assignafter");
        trueBBMap.put(rhs, trueBB);
        falseBBMap.put(rhs, falseBB);
        rhs.accept(this);
        trueBB.append(new MoveInstIR(dest, ONE));
        trueBB.append(new JumpInstIR(afterBB));
        falseBB.append(new MoveInstIR(dest, ZERO));
        falseBB.append(new JumpInstIR(afterBB));
        curBB = afterBB;
    }

    private void valueAssign(ExprNode lhs, ExprNode rhs) {
        rhs.accept(this);
        curBB.append(new MoveInstIR(lhs.resultReg, rhs.resultReg));
    }

    @Override
    public void visit(AssignExprNode node) {
        ExprNode lhs = node.getLeftExpr(), rhs = node.getRightExpr();
        lhs.accept(this);
        if (rhs.getType() instanceof BoolType)
            boolAssign(lhs.resultReg, rhs);
        else
            valueAssign(lhs, rhs);
    }

    @Override
    public void visit(IdentExprNode node) {
        if (node.isVar()) {
            VarSymbol var = (VarSymbol) node.getSymbol();
            if (var.vReg == null)
                throw new IRError("varReg " + node.getName() + " used before define");
            if (trueBBMap.containsKey(node)) {
                curBB.append(new CJumpInstIR(CJumpInstIR.Op.E, var.vReg, ONE,
                        trueBBMap.get(node), falseBBMap.get(node)));
            } else if (var.belongClass != null)
                node.resultReg = new MemoryIR(curThisPointor, var.belongClass.getVarOffset(node.getName()));
            else
                node.resultReg = var.vReg;
            if (currentFunc != null && var.isGlobalVar) {
                currentFunc.usedGlobalVar.add(var.vReg);
            }
        } else if (node.isFunc()) {
            throw new IRError("find FuncCall dealing with Identifier");
        }

    }

    @Override
    public void visit(ThisExprNode node) {
        node.resultReg = curThisPointor;
    }

    @Override
    public void visit(IntConstExprNode node) {
        node.resultReg = new ImmediateIR(node.getValue());
    }

    private int stringCnt = 0;

    @Override
    public void visit(StringConstExprNode node) {
        StaticDataIR staticData = new StaticDataIR(node.getString());
        root.getStaticData().add(staticData);
        MemoryIR constString = new MemoryIR(staticData);
        node.resultReg = new VirtualRegisterIR("constString_addr");
        constString.lable = staticData.lable = "constString" + (stringCnt++);
        curBB.append(new LeaInstIR(node.resultReg, constString));
    }

    @Override
    public void visit(BoolConstExprNode node) {
        if (node.getValue() == BoolConstExprNode.BoolValue.TRUE)
            curBB.append(new JumpInstIR(trueBBMap.get(node)));
        else
            curBB.append(new JumpInstIR(falseBBMap.get(node)));
    }

    @Override
    public void visit(NullExprNode node) {
        node.resultReg = ZERO;
    }


}
