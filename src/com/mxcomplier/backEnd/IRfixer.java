package com.mxcomplier.backEnd;

import com.mxcomplier.Config;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Ir.ProgramIR;
import com.mxcomplier.Ir.RegisterSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class IRfixer extends IRScanner {

    private FuncIR curFunc = null;

    private HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap = new HashMap<>();

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

        for (FuncIR func : node.getFuncs()) {

            for (BasicBlockIR bb : func.getBBList())
                for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                    inst.replaceVreg(renameMap);
                }

            HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap = new HashMap<>();
            for (VirtualRegisterIR vreg : func.selfDefinedGlobalVar) {
                VirtualRegisterIR temp = new VirtualRegisterIR(func.getName() + "_" + vreg.lable);
                temp.setMemory(vreg.getMemory());
                renameMap.put(vreg, temp);
            }
            for (VirtualRegisterIR vreg : func.selfUsedGlobalVar) {
                if (!renameMap.containsKey(vreg)) {
                    VirtualRegisterIR temp = new VirtualRegisterIR(func.getName() + "_" + vreg.lable);
                    temp.setMemory(vreg.getMemory());
                    renameMap.put(vreg, temp);
                }
            }
            for (BasicBlockIR bb : func.getBBList()) {
                for (InstIR inst = bb.getTail().prev; inst != bb.getHead(); inst = inst.prev)
                    inst.replaceVreg(renameMap);
            }
        }
    }

    @Override
    public void visit(FuncIR node) {

        int i = 0;
        for (AddressIR arg : node.getParameters()) {
            if (i < 6)
                node.entryBB.getHead().append(new MoveInstIR(arg, RegisterSet.paratReg[i]));
            else {
                node.entryBB.getHead().append(new MoveInstIR(arg,
                        new MemoryIR(RegisterSet.Vrbp, Config.getREGSIZE() * (i - 6 + 2))));
            }
            i++;
        }

        HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap = new HashMap<>();
        renameMap.put(node.getReturnValue(), RegisterSet.Vrax);
        for (BasicBlockIR bb : node.getBBList()) {
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                inst.replaceVreg(renameMap);
            }
        }


        InstIR firstInst = node.entryBB.getHead().next;
        if (firstInst instanceof CallInstIR && ((CallInstIR) firstInst).getFunc().getName().equals("__init")) {
            firstInst = firstInst.next;
        }

        for (VirtualRegisterIR vreg : node.selfUsedGlobalVar)
            firstInst.prepend(new MoveInstIR(vreg, vreg.getMemory()));

        if (!(node.getName().equals("__init") || node.getName().equals("main")))
            for (VirtualRegisterIR vreg : node.selfDefinedGlobalVar)
                node.leaveBB.getTail().prev.prepend(new MoveInstIR(vreg.getMemory(), vreg));

        for (BasicBlockIR bb : node.getBBList()) {
            bb.accept(this);
        }
    }

    @Override
    public void visit(MoveInstIR node) {
        if (node.getDest() instanceof MemoryIR && (node.getSrc() instanceof MemoryIR || node.getSrc() instanceof ImmediateIR)) {
            VirtualRegisterIR moveTempVreg = new VirtualRegisterIR("move_tmp");
            node.prepend(new MoveInstIR(moveTempVreg, node.getSrc()));
            node.setSrc(moveTempVreg);
        }
    }

    @Override
    public void visit(LeaInstIR node) {
        if (node.getDest() instanceof MemoryIR && node.getSrc() instanceof MemoryIR) {
            VirtualRegisterIR moveTempVreg = new VirtualRegisterIR("move_tmp");
            node.append(new MoveInstIR(node.getDest(), moveTempVreg));
            node.setDest(moveTempVreg);
        }
    }

    @Override
    public void visit(BinaryInstIR node) {
        switch (node.getOp()) {
            case SHR:
            case SHL:
                if (node.getSrc() instanceof VirtualRegisterIR) {
                    node.prepend(new MoveInstIR(RegisterSet.Vrcx, node.getSrc()));
                    node.setSrc(RegisterSet.Vrcx);
                }
                break;
            case MUL:
            case DIV:
            case MOD:
                if (node.getSrc() instanceof ImmediateIR && (node.getOp() == BinaryInstIR.Op.MOD || node.getOp() == BinaryInstIR.Op.DIV)) {
                    long value = ((ImmediateIR) node.getSrc()).getValue();
                    long tmp = value;
                    int K = 32;
                    while (tmp > 1) {
                        tmp >>= 1;
                        K++;
                    }
                    long magicValue = ((((long) 1 << K) + value - 1)) / value;
                    VirtualRegisterIR res = (VirtualRegisterIR) node.getDest();
                    node.prepend(new MoveInstIR(RegisterSet.Vrax, node.getDest()));
                    node.prepend(new MoveInstIR(RegisterSet.Vrcx, new ImmediateIR(magicValue)));
                    node.prepend(new BinaryInstIR(BinaryInstIR.Op.MUL, RegisterSet.Vrax, RegisterSet.Vrcx));
                    node.prepend(new BinaryInstIR(BinaryInstIR.Op.SHR, RegisterSet.Vrax, new ImmediateIR(K)));
                    if (node.getOp() == BinaryInstIR.Op.MOD) {
                        node.prepend(new MoveInstIR(RegisterSet.Vrcx, node.getSrc()));
                        node.prepend(new BinaryInstIR(BinaryInstIR.Op.MUL, RegisterSet.Vrax, RegisterSet.Vrcx));
                        node.prepend(new BinaryInstIR(BinaryInstIR.Op.SUB, res, RegisterSet.Vrax));
                    } else
                        node.prepend(new MoveInstIR(res, RegisterSet.Vrax));
                    node.remove();
                    return;
                }
                node.prepend(new MoveInstIR(RegisterSet.Vrax, node.getDest()));
                node.prepend(new MoveInstIR(RegisterSet.Vr11, node.getSrc()));
                node.setSrc(RegisterSet.Vr11);
                node.prepend(new BinaryInstIR(BinaryInstIR.Op.XOR, RegisterSet.Vrdx, RegisterSet.Vrdx));
                if (node.getOp() == BinaryInstIR.Op.MOD)
                    node.append(new MoveInstIR(node.getDest(), RegisterSet.Vrdx));
                else
                    node.append(new MoveInstIR(node.getDest(), RegisterSet.Vrax));
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(PushInstIR node) {
        if (node.getSrc() instanceof ImmediateIR) {
            VirtualRegisterIR moveTempVreg = new VirtualRegisterIR("move_tmp");
            node.prepend(new MoveInstIR(moveTempVreg, node.getSrc()));
            node.setSrc(moveTempVreg);
        }
    }

    @Override
    public void visit(CJumpInstIR node) {
        if (node.getLhs() instanceof ImmediateIR) {
            node.swap();
        }

        if (node.getLhs() instanceof MemoryIR && node.getRhs() instanceof MemoryIR) {
            VirtualRegisterIR moveTempVreg = new VirtualRegisterIR("move_tmp");
            node.prepend(new MoveInstIR(moveTempVreg, node.getRhs()));
            node.setRhs(moveTempVreg);
        }
    }

    @Override
    public void visit(CallInstIR node) {
        if (node.getReturnValue() != null)
            node.append(new MoveInstIR(node.getReturnValue(), RegisterSet.Vrax));
        FuncIR caller = curFunc;
        FuncIR callee = node.getFunc();
        if (callee.getType() == FuncIR.Type.USER) {
            HashSet<VirtualRegisterIR> globalVar = new HashSet<>(caller.selfDefinedGlobalVar);
            globalVar.retainAll(callee.usedGlobalVar);
            for (VirtualRegisterIR vreg : globalVar)
                node.prepend(new MoveInstIR(vreg.getMemory(), vreg));
            globalVar = new HashSet<>(caller.selfUsedGlobalVar);
            globalVar.retainAll(callee.definedGlobalVar);
            for (VirtualRegisterIR vreg : globalVar)
                node.append(new MoveInstIR(vreg, vreg.getMemory()));
        }

        LinkedList<OperandIR> args = new LinkedList<>(node.getArgs());
        while (args.size() > 6)
            node.prepend(new PushInstIR(args.removeLast()));
        for (int i = args.size() - 1; i >= 0; i--) {
            OperandIR arg = node.getArgs().get(i);
            node.prepend(new MoveInstIR(RegisterSet.paratReg[i], arg));
        }
    }

}
