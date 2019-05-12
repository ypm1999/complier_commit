package com.mxcomplier.backEnd;

import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.*;

public class FuncInliner extends IRScanner {

    static private final int MAX_CALLEE_INST_NUM = 1 << 9;
    static private final int MAX_CALLER_INST_NUM = 1 << 12;
    static private final int MAX_INLINE_RAND = 1;

    private class FuncInfo {
        int instNum = 0;
        boolean selfRecursive = false;
    }

    private HashMap<FuncIR, FuncInfo> funcInfoMap = new HashMap<>();
    private HashMap<FuncIR, FuncIR> funcBuckupMap = new HashMap<>();

    private FuncIR doBuckup(FuncIR oldFunc) {
        Map<BasicBlockIR, BasicBlockIR> bbRenameMap = new HashMap<>();
        Map<VirtualRegisterIR, VirtualRegisterIR> vregRenameMap = new HashMap<>();
        FuncIR newfunc = new FuncIR(oldFunc.getName(), oldFunc.getType());

        for (int i = 0; i < oldFunc.getParameters().size(); i++) {
            VirtualRegisterIR oldArg = oldFunc.getParameters().get(i);
            VirtualRegisterIR newArg = new VirtualRegisterIR(oldArg.lable + "_");
            vregRenameMap.put(oldArg, newArg);
            newfunc.getParameters().add(newArg);
        }

        if (oldFunc.returnValue != null) {
            VirtualRegisterIR newret = new VirtualRegisterIR(oldFunc.returnValue.lable + "_");
            newfunc.returnValue = newret;
            if (!vregRenameMap.containsKey(oldFunc.returnValue))
                vregRenameMap.put(oldFunc.returnValue, newret);
        }

        for (VirtualRegisterIR vreg : oldFunc.getAllVreg())
            if (!vregRenameMap.containsKey(vreg) && !oldFunc.usedGlobalVar.contains(vreg))
                vregRenameMap.put(vreg, new VirtualRegisterIR(vreg.lable + "_"));
        for (BasicBlockIR bb : oldFunc.getBBList()) {
            bbRenameMap.put(bb, new BasicBlockIR(newfunc, bb.getLable() + "_"));
        }

        newfunc.entryBB = bbRenameMap.get(oldFunc.entryBB);
        newfunc.leaveBB = bbRenameMap.get(oldFunc.leaveBB);
        newfunc.usedGlobalVar = oldFunc.usedGlobalVar;
        newfunc.definedGlobalVar = oldFunc.definedGlobalVar;
        newfunc.selfUsedGlobalVar = oldFunc.selfUsedGlobalVar;
        newfunc.selfDefinedGlobalVar = oldFunc.selfDefinedGlobalVar;

        for (BasicBlockIR oldBB : oldFunc.getBBList()) {
            BasicBlockIR newBB = bbRenameMap.get(oldBB);
            for (InstIR inst = oldBB.getHead().next; inst != oldBB.getTail(); inst = inst.next) {
                InstIR newinst = inst.copy();
                newinst.replaceVreg(vregRenameMap);
                if (newinst instanceof BranchInstIR)
                    ((BranchInstIR) newinst).bbRename(bbRenameMap);
                newBB.append(newinst);
            }
        }

        return newfunc;
    }

    public void run(IRBuilder ir) {
        List<FuncIR> funcList = ir.root.getFuncs();
        for (FuncIR func : funcList) {
            FuncInfo info = new FuncInfo();
            funcInfoMap.put(func, info);
            info.instNum = func.getInstNum();
            info.selfRecursive = func.callee.contains(func);
        }

        boolean changed = true;
        for (int cnt = 0; changed && cnt < MAX_INLINE_RAND; cnt++) {
            changed = false;
            funcBuckupMap.clear();
            for (FuncIR func : funcList) {
                if (funcInfoMap.get(func).instNum > MAX_CALLER_INST_NUM)
                    continue;
                List<BasicBlockIR> tempBBList = new ArrayList<>(func.getBBList());
                for (BasicBlockIR bb : tempBBList) {
                    BasicBlockIR curBB = bb;
                    for (InstIR inst = bb.getHead().next; !(inst instanceof EmptyInstIR); inst = inst.next) {
                        if (inst instanceof CallInstIR) {
                            CallInstIR call = (CallInstIR) inst;
                            FuncInfo info = funcInfoMap.getOrDefault(call.getFunc(), null);
                            if (info == null || info.instNum > MAX_CALLEE_INST_NUM)
                                continue;

                            if (func == call.getFunc()) {
                                if (!funcBuckupMap.containsKey(func))
                                    funcBuckupMap.put(func, doBuckup(func));
                                doInline(func, funcBuckupMap.get(func), curBB, call);
                            } else
                                curBB = doInline(func, call.getFunc(), curBB, call);
                            inst = curBB.getHead();
//                            new IRPrinter(ir).visit(ir.root);
                            funcInfoMap.get(func).instNum += info.instNum - 1;
                            funcInfoMap.get(func).selfRecursive = func.callee.contains(func);
                            changed = true;
//                            break;
                        }
                    }
                }
            }

            List<FuncIR> tmp = new ArrayList<>(funcList);
            for (FuncIR func : tmp) {
                if (func.caller.isEmpty() && !func.getName().equals("main"))
                    funcList.remove(func);
            }
        }

    }

    private BasicBlockIR doInline(FuncIR caller, FuncIR callee, BasicBlockIR curBB, CallInstIR call) {
        Map<BasicBlockIR, BasicBlockIR> bbRenameMap = new HashMap<>();
        Map<VirtualRegisterIR, VirtualRegisterIR> vregRenameMap = new HashMap<>();

        callee.initOrderBBList();
        List<BasicBlockIR> calleeBBList = callee.getReversedOrderedBBList();

        BasicBlockIR oldLeaveBB = callee.leaveBB;
        BasicBlockIR newLeaveBB = new BasicBlockIR(caller, oldLeaveBB.getLable() + "_inline");
        bbRenameMap.put(oldLeaveBB, newLeaveBB);
        bbRenameMap.put(callee.entryBB, curBB);

        newLeaveBB.getHead().next = call.next;
        call.next.prev = newLeaveBB.getHead();
        newLeaveBB.getTail().prev = curBB.getTail().prev;
        curBB.getTail().prev.next = newLeaveBB.getTail();
        curBB.getTail().prev = call;
        call.next = curBB.getTail();

        //rename inst after call
        for (InstIR inst = newLeaveBB.getHead().next; inst != newLeaveBB.getTail(); inst = inst.next) {
            if (inst instanceof BranchInstIR)
                ((BranchInstIR) inst).bbRename(Collections.singletonMap(curBB, newLeaveBB));
        }

        for (int i = 0; i < callee.getParameters().size(); i++) {
            VirtualRegisterIR oldArg = callee.getParameters().get(i);
            VirtualRegisterIR newArg = new VirtualRegisterIR(oldArg);
            call.prepend(new MoveInstIR(newArg, call.getArgs().get(i)));
            vregRenameMap.put(oldArg, newArg);
        }

        call.remove();

        for (BasicBlockIR bb : calleeBBList) {
            if (!bbRenameMap.containsKey(bb)) {
                bbRenameMap.put(bb, new BasicBlockIR(caller, bb.getLable() + "_inline"));
            }
        }

        if (callee.returnValue != null)
            vregRenameMap.put(callee.returnValue, (VirtualRegisterIR) call.getReturnValue());

        for (VirtualRegisterIR vreg : callee.getAllVreg()) {
            if (!vregRenameMap.containsKey(vreg) && !callee.usedGlobalVar.contains(vreg)) {
                vregRenameMap.put(vreg, new VirtualRegisterIR(vreg));
            }
        }

        for (BasicBlockIR oldBB : calleeBBList) {
            if (oldBB == callee.leaveBB)
                continue;
            BasicBlockIR newBB = bbRenameMap.get(oldBB);
            for (InstIR inst = oldBB.getHead().next; inst != oldBB.getTail(); inst = inst.next) {
                InstIR newinst = inst.copy();
                newinst.replaceVreg(vregRenameMap);
                if (newinst instanceof BranchInstIR)
                    ((BranchInstIR) newinst).bbRename(bbRenameMap);
                newBB.append(newinst);
            }
        }

        if (callee.getType() != FuncIR.Type.LIBRARY) {
            caller.updateCallee();
            if (callee != caller) {
                caller.usedGlobalVar.addAll(callee.usedGlobalVar);
//                caller.definedGlobalVar.addAll(callee.definedGlobalVar);
//                caller.selfUsedGlobalVar.addAll(callee.selfUsedGlobalVar);
//                caller.selfDefinedGlobalVar.addAll(callee.selfDefinedGlobalVar);
            }
        }

        return newLeaveBB;
    }
}
