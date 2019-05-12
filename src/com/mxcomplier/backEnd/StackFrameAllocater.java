package com.mxcomplier.backEnd;

import com.mxcomplier.Config;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.ImmediateIR;
import com.mxcomplier.Ir.Operands.PhysicalRegisterIR;
import com.mxcomplier.Ir.Operands.StackSoltIR;
import com.mxcomplier.Ir.ProgramIR;
import com.mxcomplier.Ir.RegisterSet;

import java.util.HashSet;

import static java.lang.Float.min;

public class StackFrameAllocater extends IRScanner {
    private FuncIR curFunc = null;


    @Override
    public void visit(BasicBlockIR node) {
        InstIR inst = node.getHead().next;
        while (inst != node.getTail()) {
            inst.accept(this);
            inst = inst.next;
        }
    }

    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs()) {
            curFunc = func;
            func.accept(this);
            curFunc = null;
        }
    }


    @Override
    public void visit(FuncIR node) {
        InstIR firstInst = node.entryBB.getHead().next;
        firstInst.prepend(new PushInstIR(RegisterSet.rbp));
        firstInst.prepend(new MoveInstIR(RegisterSet.rbp, RegisterSet.rsp));

        HashSet<StackSoltIR> stackSolts = new HashSet<>();
        for (BasicBlockIR bb : node.getBBList()) {
            InstIR inst = bb.getHead().next;
            while (inst != bb.getTail()) {
                stackSolts.addAll(inst.getStackSolt());
                inst = inst.next;
            }
        }

        int stackSize = (stackSolts.size()) * Config.getREGSIZE();
        firstInst.prepend(new BinaryInstIR(BinaryInstIR.Op.SUB, RegisterSet.rsp, new ImmediateIR(stackSize)));

        int i = 0;
        for (StackSoltIR stackSolt : stackSolts)
            stackSolt.setNum(-Config.getREGSIZE() * (++i));

        //TODO callee save regs
        HashSet<PhysicalRegisterIR> saveSet = new HashSet<>(RegisterSet.calleeSaveRegisterSet);

//        saveSet.retainAll(node.getDefinedPhyRegs());
        if (!node.getName().equals("main"))
            for (PhysicalRegisterIR preg : saveSet) {
                firstInst.prepend(new PushInstIR(preg));
                firstInst = firstInst.prev;
            }

        for (BasicBlockIR bb : node.getBBList()) {
            bb.accept(this);
        }

        if (!node.getName().equals("main")) {
            InstIR lastInst = node.leaveBB.getTail().prev;
            for (PhysicalRegisterIR preg : saveSet)
                lastInst.prepend(new PopInstIR(preg));
        }
    }

    @Override
    public void visit(CallInstIR node) {

        //TODO caller save regs
        HashSet<PhysicalRegisterIR> saveSet = new HashSet<>(RegisterSet.callerSaveRegisterSet);
//        saveSet.retainAll(node.getFunc().getDefinedPhyRegs());
//        saveSet.retainAll(curFunc.getUsedPhyRegs());
        for (int i = 0; i < min(6, node.getArgs().size()); ++i)
            saveSet.remove(RegisterSet.paratReg[i].getPhyReg());
        InstIR firstInst = node;
        int cnt = node.getArgs().size() - 6;
        while(cnt > 0){
            firstInst = firstInst.prev;
            if (firstInst instanceof PushInstIR)
                cnt--;
        }
//        if (!node.getFunc().getName().equals("__init"))
            for (PhysicalRegisterIR preg : saveSet)
                firstInst.prepend(new PushInstIR(preg));

//        if (!node.getFunc().getName().equals("__init"))
            for (PhysicalRegisterIR preg : saveSet)
                node.append(new PopInstIR(preg));
        if (node.getArgs().size() > 6)
            node.append(new BinaryInstIR(BinaryInstIR.Op.ADD, RegisterSet.rsp,
                    new ImmediateIR(Config.getREGSIZE() * (node.getArgs().size() - 6))));
    }
}

