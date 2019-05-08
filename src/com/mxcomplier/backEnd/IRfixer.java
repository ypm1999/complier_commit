package com.mxcomplier.backEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.ImmediateIR;
import com.mxcomplier.Ir.Operands.MemoryIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Ir.ProgramIR;
import com.mxcomplier.Ir.RegisterSet;

import java.util.*;

import static com.mxcomplier.FrontEnd.IRBuilder.ZERO;

public class IRfixer extends IRScanner {

    private FuncIR curFunc = null;

    @Override
    public void visit(BasicBlockIR node) {
        InstIR inst = node.getHead().next;
        while(inst != node.getTail()){
            inst.accept(this);
            inst = inst.next;
        }
    }

    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs()){
            curFunc = func;
            dfsGlobalVars(func);
            func.accept(this);
            curFunc = null;
        }
        for (FuncIR func : node.getFuncs()){
            HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap = new HashMap<>();
            for (VirtualRegisterIR vreg : func.usedGlobalVar) {
                VirtualRegisterIR temp = new VirtualRegisterIR(func.getName() + "_" + vreg.lable);
                temp.memory = vreg.memory;
                renameMap.put(vreg, temp);
            }
            for (BasicBlockIR bb : func.getBBList()) {
                for (InstIR inst = bb.getTail().prev; inst != bb.getHead(); inst = inst.prev)
                    inst.replaceVreg(renameMap);
            }
        }
    }

    static private HashSet<FuncIR> accessedFunc = new HashSet<>();
    private void dfsGlobalVars(FuncIR func){
        if (accessedFunc.contains(func))
            return;
        accessedFunc.add(func);
        func.selfUsedGlobalVar = new HashSet<>(func.usedGlobalVar);
        for (FuncIR nextFunc:func.callee){
            dfsGlobalVars(nextFunc);
            func.usedGlobalVar.addAll(nextFunc.usedGlobalVar);
        }
    }

    @Override
    public void visit(FuncIR node) {
        for (BasicBlockIR bb : node.getBBList()){
            bb.accept(this);
        }
    }

    @Override
    public void visit(MoveInstIR node) {
        if (node.getDest() instanceof MemoryIR && (node.getSrc() instanceof MemoryIR || node.getSrc() instanceof  ImmediateIR)){
            VirtualRegisterIR tmp = new VirtualRegisterIR("move_tmp");
            node.prepend(new MoveInstIR(tmp, node.getSrc()));
            node.setSrc(tmp);
        }
    }

    @Override
    public void visit(LeaInstIR node) {
        if (node.getDest() instanceof MemoryIR && node.getSrc() instanceof MemoryIR){
            VirtualRegisterIR tmp = new VirtualRegisterIR("lea_tmp");
            node.append(new MoveInstIR(node.getDest(), tmp));
            node.dest = tmp;
        }
    }

    @Override
    public void visit(BinaryInstIR node) {
        switch (node.getOp()){
            case SHR:
            case SHL:
                if (node.getSrc() instanceof VirtualRegisterIR){
                    node.prepend(new MoveInstIR(RegisterSet.Vrcx, node.src));
                    node.src = RegisterSet.Vrcx;
                }
                break;
            case MUL:
            case DIV:
            case MOD:
                node.prepend(new MoveInstIR(RegisterSet.Vrax, node.dest));
                node.prepend(new MoveInstIR(RegisterSet.Vrbx, node.src));
                node.prepend(new MoveInstIR(RegisterSet.Vrdx, ZERO));
                node.src = RegisterSet.Vrbx;
                if (node.getOp() == BinaryInstIR.Op.MOD)
                    node.append(new MoveInstIR(node.dest, RegisterSet.Vrdx));
                else
                    node.append(new MoveInstIR(node.dest, RegisterSet.Vrax));
                break;
            default: break;
        }
    }

    @Override
    public void visit(PushInstIR node) {
        if (node.getSrc() instanceof ImmediateIR){
            VirtualRegisterIR tmp = new VirtualRegisterIR("push_tmp");
            node.prepend(new MoveInstIR(tmp, node.getSrc()));
            node.setSrc(tmp);
        }
    }

    @Override
    public void visit(CJumpInstIR node) {
        if (node.getLhs() instanceof ImmediateIR){
            if (node.getRhs() instanceof ImmediateIR)
                node.prepend(new MoveInstIR(new VirtualRegisterIR("Cjump_imm_temp"), node.getLhs()));
            else
                node.swap();
        }

        if (node.getLhs() instanceof MemoryIR && node.getRhs() instanceof MemoryIR){
            VirtualRegisterIR tmp = new VirtualRegisterIR("Cjump_tmp");
            node.prepend(new MoveInstIR(tmp, node.getRhs()));
            node.rhs = tmp;
        }
        node.append(new JumpInstIR(node.getFalseBB()));
    }

    @Override
    public void visit(CallInstIR node) {
        FuncIR caller = curFunc;
        FuncIR callee = node.getFunc();
        if (callee.getType() == FuncIR.Type.USER) {
            HashSet<VirtualRegisterIR> globalVar = new HashSet<>(caller.selfUsedGlobalVar);
            globalVar.retainAll(callee.usedGlobalVar);
            for (VirtualRegisterIR vreg : globalVar) {
                node.prepend(new MoveInstIR(vreg.memory, vreg));
                node.append(new MoveInstIR(vreg, vreg.memory));
            }
        }

        LinkedList<OperandIR> args = new LinkedList<>(node.getArgs());
        while(args.size() > 6)
            node.prepend(new PushInstIR(args.removeLast()));
        for (int i = args.size()-1; i >= 0; i--){
            OperandIR arg = node.getArgs().get(i);
            node.prepend(new MoveInstIR(RegisterSet.paratReg[i], arg));
        }
    }

}
