package com.mxcomplier.FrontEnd;

import com.mxcomplier.AST.*;
import com.mxcomplier.Config;
import com.mxcomplier.Error.IRError;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Ir.ProgramIR;
import com.mxcomplier.Ir.RegisterSet;
import com.mxcomplier.Scope.*;
import com.mxcomplier.Type.*;

import java.util.*;

public class IRBuilder extends ASTScanner{
    public ProgramIR root;
    public Map<String, FuncIR> funcMap = new HashMap<>();
    private Map<ExprNode, BasicBlockIR> trueBBMap = new HashMap<>();
    private Map<ExprNode, BasicBlockIR> falseBBMap = new HashMap<>();
    private FuncIR initFunc= null;

    private FuncIR currentFunc;
    private BasicBlockIR curBB, curLoopAfter, curLoopCondition;
    private VirtualRegisterIR curThisPointor;

    private ImmediateIR ONE = new ImmediateIR(1), ZERO = new ImmediateIR(0);
    private ImmediateIR REGSIZE = new ImmediateIR(Config.getREGSIZE());

    private FuncIR library_malloc, library_strcmp, library_stradd;

    private RegisterIR memoryMove(AddressIR reg){
        if (!(reg instanceof RegisterIR)){
            VirtualRegisterIR tmp = new VirtualRegisterIR(reg.lable + "_to_reg");
            curBB.append(new MoveInstIR(tmp, reg));
            return tmp;
        }
        else
            return (RegisterIR) reg;
    }

    private String transName(String className, String funcName){
        return "_" + className + '_' + funcName;
    }

    private void initBuildInFunc(){
        //TODO

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

    public IRBuilder(){
        //TODO

        initBuildInFunc();
    }

    private void addVarInitInst(RegisterIR reg, ExprNode initExpr){
        if (initExpr.getType() instanceof BoolType){
            boolAssign(reg, initExpr);
        }
        else {
            initExpr.accept(this);
            curBB.append(new MoveInstIR(reg, initExpr.resultReg));
        }
    }

    private void initFunc(FuncDefNode node, String className){
        FuncIR func = new FuncIR(className + node.getName());
        funcMap.put(func.getName(), func);
        node.setFuncIR(func);
        root.getFuncs().add(func);
    }

    @Override
    public void visit(ProgramNode node) {
        globalScope = currentScope = node.getScope();
        root = new ProgramIR();

        root.getFuncs().add(initFunc);
        for (Node section: node.getSections())
            if (section instanceof FuncDefNode){
                initFunc((FuncDefNode) section,  "");
            }
            else if (section instanceof ClassDefNode){
                for (FuncDefNode func : ((ClassDefNode) section).getFuncDefs())
                    initFunc(func, '_' + ((ClassDefNode) section).getName() + '_');
            }

        curBB = initFunc.entryBB = new BasicBlockIR(initFunc, "initFuncEntry");
        currentFunc = initFunc;
        for (Node section: node.getSections())
            if (section instanceof VarDefNode){
                VarSymbol var = currentScope.getVar(((VarDefNode) section).getName());
                var.vReg = new VirtualRegisterIR(((VarDefNode) section).getName());
                StaticDataIR staticData = new StaticDataIR();
                staticData.lable = var.getName();
                root.getStaticData().add(staticData);
                var.vReg.memory = new MemoryIR(staticData);
                if (((VarDefNode) section).getInitExpr() != null)
                    addVarInitInst(var.vReg, ((VarDefNode) section).getInitExpr());
            }
//        FuncIR main_func = funcMap.get("main");
//        curBB.append(new CallInstIR(main_func, new ArrayList<>()));
        if (!(curBB.getTail().prev instanceof BranchInstIR))
            curBB.append(new ReturnInstIR(null));
        initFunc.leaveBB = curBB;

        for (Node section: node.getSections())
            if (!(section instanceof VarDefNode))
                section.accept(this);

        globalScope = currentScope = currentScope.getParent();
    }

    @Override
    public void visit(FuncDefNode node) {

        currentFunc = node.getFuncIR();
        curBB = currentFunc.entryBB = new BasicBlockIR(currentFunc, "entry_" + currentFunc.getName());
        //TODO add parameters
        Scope funcScope = node.getFuncBody().getScope();
        List<VirtualRegisterIR> args = currentFunc.getParameters();
        ClassSymbol belongClass = funcScope.getFunc(node.getName()).getBelongClass();
        if (belongClass != null) {
            curThisPointor = new VirtualRegisterIR("this_of_" + belongClass.getName());
            args.add(curThisPointor);
        }
        for (VarDefNode arg : node.getParameters()){
            VarSymbol var = funcScope.getVar(arg.getName());
            var.vReg = new VirtualRegisterIR(node.getName() + "_arg_" + arg.getName());
            args.add(var.vReg);
        }

        if (node.getName().equals("main")){
            curBB.append(new CallInstIR(funcMap.get("__init"), new ArrayList<>(), null));
        }

        node.getFuncBody().accept(this);

        //TODO merge return && find leaveBB
        for (BasicBlockIR bb : currentFunc.getBBList()){
            if (! (bb.getTail().prev instanceof BranchInstIR))
                bb.append(new ReturnInstIR(null));
        }

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
        if (node.getElseStmt() != null){
            BasicBlockIR elseBB = new BasicBlockIR(currentFunc, "Ifelse");
            falseBBMap.put(node.getJudgeExpr(), elseBB);
            node.getJudgeExpr().accept(this);

            curBB = elseBB;
            node.getElseStmt().accept(this);
            elseBB.append(new JumpInstIR(afterBB));
        }
        else{
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
        if (node.getExpr1() != null)
            node.getExpr1().accept(this);
        curBB.append(new JumpInstIR(condBB));

        if (node.getExpr2() == null){
            if (node.getExpr3() != null) {
                curBB = expr3BB;
                node.getExpr3().accept(this);
            }
            expr3BB.append(new JumpInstIR(condBB));
            condBB.append(new JumpInstIR(bodyBB));
        }
        else{
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
        VirtualRegisterIR res = null;
        if (node.getReturnExpr() != null) {
            res = new VirtualRegisterIR("ret_value");
            if (node.getReturnExpr().getType() instanceof BoolType){
                boolAssign(res, node.getReturnExpr());
            }
            else {
                node.getReturnExpr().accept(this);
                curBB.append(new MoveInstIR(res, node.getReturnExpr().resultReg));
            }
        }
        curBB.append(new ReturnInstIR(res));
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
        }
        else {
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


        if (func.getBelongClass() != null){
            if (node.getBaseExpr() instanceof MemberCallExprNode) {
                node.getBaseExpr().accept(this);
                args.add(node.getBaseExpr().resultReg);
            }
            else
                args.add(curThisPointor);
        }

        for (ExprNode arg : node.getArgumentList()){
            arg.accept(this);
            args.add(arg.resultReg);
        }
        curBB.append(new CallInstIR(funcMap.get(funcName), args, returnValue));
        if (trueBBMap.containsKey(node)){
            curBB.append(new CJumpInstIR(CJumpInstIR.Op.E, ONE, returnValue,
                                         trueBBMap.get(node), falseBBMap.get(node)));
        }
        else
            node.resultReg = returnValue;
    }

    @Override
    public void visit(ArrCallExprNode node) {
        node.getSubscriptExpr().accept(this);
        node.getBaseExpr().accept(this);
        RegisterIR offset = memoryMove(node.getSubscriptExpr().resultReg);
        RegisterIR base = memoryMove(node.getBaseExpr().resultReg);
        MemoryIR memory;
        if (offset instanceof ImmediateIR)
            memory = new MemoryIR(base, (int)((ImmediateIR) offset).getValue() * Config.getREGSIZE());
        else {
            memory = new MemoryIR(base, offset);
            memory.setScale(Config.getREGSIZE());
        }
        memory.lable = "arrCall";
        if (trueBBMap.containsKey(node)){
            curBB.append(new CJumpInstIR(CJumpInstIR.Op.E, ONE, memory,
                    trueBBMap.get(node), falseBBMap.get(node)));
        }
        else
            node.resultReg = memory;
    }

    @Override
    public void visit(MemberCallExprNode node) {
        node.getBaseExpr().accept(this);
        AddressIR baseExpr = node.getBaseExpr().resultReg;
        AddressIR res = null;
        if (node.getBaseExpr().getType() instanceof ArrayType || node.getBaseExpr().getType() instanceof StringType){
            res = baseExpr;
        }
        else if (node.getBaseExpr().getType() instanceof ClassType){
            String className = ((ClassType) node.getBaseExpr().getType()).getName();
            ClassSymbol classSymbol = globalScope.getClass(className);
            if (classSymbol.getScope().tryGetFunc(node.getMemberName()) != null){
                res = baseExpr;
            }
            else{
                res = new MemoryIR(memoryMove(baseExpr), classSymbol.getVarOffset(node.getMemberName()));
                res.lable = "memCall";
            }
        }
        else assert false;
        if (trueBBMap.containsKey(node)){
            curBB.append(new CJumpInstIR(CJumpInstIR.Op.E, ONE, res,
                    trueBBMap.get(node), falseBBMap.get(node)));
        }
        else
            node.resultReg = res;
    }

    @Override
    public void visit(PrefixExprNode node) {
        UnaryInstIR.Op op = UnaryInstIR.Op.ERROR;
        switch (node.getPrefixOp()){
            case PREFIX_INC : op = UnaryInstIR.Op.INC; break;
            case PREFIX_DEC : op = UnaryInstIR.Op.DEC; break;
            case PLUS       : op = UnaryInstIR.Op.NULL; break;
            case MINUS      : op = UnaryInstIR.Op.NEG; break;
            case INV        : op = UnaryInstIR.Op.INV; break;
            case NOT        : {
                BasicBlockIR trueBB = trueBBMap.get(node);
                BasicBlockIR falseBB = falseBBMap.get(node);
                trueBBMap.put(node.getSubExpr(), falseBB);
                falseBBMap.put(node.getSubExpr(), trueBB);
                node.getSubExpr().accept(this);
                return;
            }
            default: assert false;
        }
        node.getSubExpr().accept(this);
        node.resultReg = node.getSubExpr().resultReg;
        curBB.append(new UnaryInstIR(op, node.resultReg));
    }

    private VirtualRegisterIR allocaClass(String name){
        VirtualRegisterIR res = new VirtualRegisterIR("new_Class");
        if (name.equals("string")){
            curBB.append(new CallInstIR(library_malloc,
                    Collections.singletonList(new ImmediateIR(Config.getREGSIZE() * 2)), res));
            curBB.append(new MoveInstIR(res, ZERO));
            curBB.append(new BinaryInstIR(BinaryInstIR.Op.ADD, res, REGSIZE));
            curBB.append(new MoveInstIR(res, ZERO));
            curBB.append(new BinaryInstIR(BinaryInstIR.Op.SUB, res, REGSIZE));
        }
        else{
            ClassSymbol symbol = globalScope.getClass(name);
            curBB.append(new CallInstIR(library_malloc, Collections.singletonList(new ImmediateIR(symbol.getSize())), res));
            FuncIR constructor = funcMap.getOrDefault('_' + name + '_' + name, null);
            if (constructor != null) {
                VirtualRegisterIR tmp = new VirtualRegisterIR("tmp");
                curBB.append(new MoveInstIR(tmp, res));
                curBB.append(new CallInstIR(constructor, Collections.singletonList(tmp), res));
            }
        }
        return res;
    }

    private VirtualRegisterIR allocaArray(int order, List<RegisterIR> dims){
        assert(order > 0 && !dims.isEmpty());
        VirtualRegisterIR res = new VirtualRegisterIR("arrayNew");
        RegisterIR dim = dims.get(0);
        dims.remove(0);

        VirtualRegisterIR size = new VirtualRegisterIR("new_size");
        curBB.append(new MoveInstIR(size, dim));
        curBB.append(new UnaryInstIR(UnaryInstIR.Op.INC, size));
        curBB.append(new BinaryInstIR(BinaryInstIR.Op.SHL, size, new ImmediateIR(3)));
        curBB.append(new CallInstIR(library_malloc, Collections.singletonList(size), res));
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
        curBB.append(new MoveInstIR(new MemoryIR(res, cnt, 8), allocaArray(order - 1, dims)));

        curBB.append(new UnaryInstIR(UnaryInstIR.Op.INC, cnt));
        curBB.append(new JumpInstIR(condBB));
        curBB = afterBB;
        return res;
    }

    @Override
    public void visit(NewExprNode node) {
        if (node.getDims().isEmpty()) {
            if (node.getBaseType().getType() instanceof  ClassType){
                node.resultReg = allocaClass(((ClassType) node.getBaseType().getType()).getName());
            }
            else if (node.getBaseType().getType() instanceof  StringType)
                node.resultReg = allocaClass("string");
            else
                throw new IRError("new " + node.getBaseType().getType() + " is invalid");
        }
        else {
            List<RegisterIR> dims = new ArrayList<>();
            for(ExprNode dim : node.getDims()){
                dim.accept(this);
                dims.add(memoryMove(dim.resultReg));
            }
            node.resultReg = allocaArray(node.getOrder(), dims);
        }
    }

    private void doLogicBinaryExpr(BinaryExprNode node, ExprNode lhs, ExprNode rhs){
        BasicBlockIR nextLogicBB = new BasicBlockIR(currentFunc, "logicNext");

        if (node.getOp() == BinaryExprNode.Op.ANDAND){
            trueBBMap.put(lhs, nextLogicBB);
            falseBBMap.put(lhs, falseBBMap.get(node));
        }
        else{
            trueBBMap.put(lhs, trueBBMap.get(node));
            falseBBMap.put(lhs, nextLogicBB);
        }
        lhs.accept(this);

        curBB = nextLogicBB;
        trueBBMap.put(rhs, trueBBMap.get(node));
        falseBBMap.put(rhs, falseBBMap.get(node));
        rhs.accept(this);
    }

    private void doRelationBinaryExpr(BinaryExprNode node, ExprNode lhs, ExprNode rhs){
        CJumpInstIR.Op op = CJumpInstIR.Op.ERROR;
        switch (node.getOp()){
            case LESS       : op = CJumpInstIR.Op.L; break;
            case LARGE      : op = CJumpInstIR.Op.G; break;
            case LESS_EQUAL : op = CJumpInstIR.Op.LE; break;
            case LARGE_EQUAL: op = CJumpInstIR.Op.GE; break;
            case EQUAL      : op = CJumpInstIR.Op.E; break;
            case UNEQUAL    : op = CJumpInstIR.Op.NE; break;
            default         : assert false;
        }
        lhs.accept(this);
        rhs.accept(this);
        if (node.getLeftExpr().getType() instanceof StringType){
            VirtualRegisterIR res = new VirtualRegisterIR("strcmp_returnValue");
            curBB.append(new CallInstIR(library_strcmp, Arrays.asList(lhs.resultReg, rhs.resultReg), res));
            curBB.append(new CJumpInstIR(op, res, ZERO, trueBBMap.get(node), falseBBMap.get(node)));
        }
        else {
            curBB.append(new CJumpInstIR(op, lhs.resultReg, rhs.resultReg,
                                       trueBBMap.get(node), falseBBMap.get(node)));
        }
    }

    private void doArithmeticBinaryExpr(BinaryExprNode node, ExprNode lhs, ExprNode rhs){
        VirtualRegisterIR res = new VirtualRegisterIR("airthmeticBinary");
        BinaryInstIR.Op op = BinaryInstIR.Op.ERROR;
        switch (node.getOp()){
            case MUL    : op = BinaryInstIR.Op.MUL; break;
            case DIV    : op = BinaryInstIR.Op.DIV; break;
            case MOD    : op = BinaryInstIR.Op.MOD; break;
            case PLUS   : op = BinaryInstIR.Op.ADD; break;
            case MINUS  : op = BinaryInstIR.Op.SUB; break;
            case LSH    : op = BinaryInstIR.Op.SHL; break;
            case RSH    : op = BinaryInstIR.Op.SHR; break;
            case AND    : op = BinaryInstIR.Op.AND; break;
            case XOR    : op = BinaryInstIR.Op.XOR; break;
            case OR     : op = BinaryInstIR.Op.OR; break;
            default: assert false;
        }
        lhs.accept(this);
        rhs.accept(this);
        if (op == BinaryInstIR.Op.ADD && lhs.getType() instanceof StringType){
            curBB.append(new CallInstIR(library_stradd, Arrays.asList(lhs.resultReg, rhs.resultReg), res));
        }
        else {
            curBB.append(new MoveInstIR(res, lhs.resultReg));
            curBB.append(new BinaryInstIR(op, res, rhs.resultReg));
        }
        node.resultReg = res;
    }

    @Override
    public void visit(BinaryExprNode node) {
        ExprNode lhs = node.getLeftExpr(), rhs = node.getRightExpr();
        switch (node.getOp()){
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
    private void boolAssign(AddressIR dest, ExprNode rhs){
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

    private void valueAssign(ExprNode lhs, ExprNode rhs){
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
        if (node.isVar()){
            VarSymbol var = currentScope.getVar(node.getName());
            if(var.vReg == null)
                throw new IRError("varReg " + node.getName() + " used before define");
            if (trueBBMap.containsKey(node)){
                curBB.append(new CJumpInstIR(CJumpInstIR.Op.E, var.vReg, ONE,
                                             trueBBMap.get(node), falseBBMap.get(node)));
            }
            else if (var.belongClass != null)
                node.resultReg = new MemoryIR(curThisPointor, var.belongClass.getVarOffset(node.getName()));
            else
                node.resultReg = var.vReg;
        }
        else if (node.isFunc()){
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

    @Override
    public void visit(StringConstExprNode node) {
        StaticDataIR staticData = new StaticDataIR(node.getString());
        root.getStaticData().add(staticData);
        MemoryIR constString = new MemoryIR(staticData);
        constString.lable = staticData.lable = "constString";
        node.resultReg = new VirtualRegisterIR("constString_addr");
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
