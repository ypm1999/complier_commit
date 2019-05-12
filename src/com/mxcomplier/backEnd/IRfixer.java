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

import static com.mxcomplier.FrontEnd.IRBuilder.ZERO;

public class IRfixer extends IRScanner {

    private FuncIR curFunc = null;

    @Override
    public void visit(BasicBlockIR node) {
        InstIR inst = node.getHead().next;
        while (inst != node.getTail()) {
            inst.accept(this);
            inst = inst.next;
        }
    }

//    private void removeUselessMove(List<FuncIR> funcs){
//        for (FuncIR func : funcs){
//            for (BasicBlockIR bb : func.getBBList()) {
//                for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
//                    if (inst instanceof MoveInstIR){
//                        MoveInstIR move1 = (MoveInstIR) inst;
//                        if(move1.src == move1.dest){
//                            inst = inst.prev;
//                            move1.remove();
//                        }
//                        else if (inst.next instanceof MoveInstIR) {
//                            MoveInstIR move2 = (MoveInstIR) inst.next;
//                            if (move1.dest == move2.src) {
//                                move1.dest = move2.dest;
//                                inst = inst.prev;
//                                move2.remove();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs()) {
            curFunc = func;
            func.accept(this);
            curFunc = null;
        }


        for (FuncIR func : node.getFuncs()) {
            HashMap<VirtualRegisterIR, VirtualRegisterIR> renameMap = new HashMap<>();
            for (VirtualRegisterIR vreg : func.usedGlobalVar) {
                VirtualRegisterIR temp = new VirtualRegisterIR(func.getName() + "_" + vreg.lable);
                temp.memory = vreg.memory;
                renameMap.put(vreg, temp);
            }
            for (VirtualRegisterIR vreg : func.usedGlobalVar) {
                if (!renameMap.containsKey(vreg)) {
                    VirtualRegisterIR temp = new VirtualRegisterIR(func.getName() + "_" + vreg.lable);
                    temp.memory = vreg.memory;
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
        renameMap.put(node.returnValue, RegisterSet.Vrax);
        for (BasicBlockIR bb : node.getBBList()) {
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                inst.replaceVreg(renameMap);
            }
        }


        InstIR firstInst = node.entryBB.getHead().next;
        if (firstInst instanceof CallInstIR && ((CallInstIR) firstInst).getFunc().getName().equals("__init"))
            firstInst = firstInst.next;
//        for (VirtualRegisterIR vreg : node.selfUsedGlobalVar)
        for (VirtualRegisterIR vreg : node.usedGlobalVar)
            firstInst.prepend(new MoveInstIR(vreg, vreg.memory));

        if (!node.getName().equals("__init"))
//            for (VirtualRegisterIR vreg : node.selfDefinedGlobalVar)
            for (VirtualRegisterIR vreg : node.usedGlobalVar)
                node.leaveBB.getTail().prev.prepend(new MoveInstIR(vreg.memory, vreg));

        for (BasicBlockIR bb : node.getBBList()) {
            bb.accept(this);
        }
    }

    static private final VirtualRegisterIR moveTempVreg = new VirtualRegisterIR("move_tmp");

    @Override
    public void visit(MoveInstIR node) {
        if (node.getDest() instanceof MemoryIR && (node.getSrc() instanceof MemoryIR || node.getSrc() instanceof ImmediateIR)) {
            node.prepend(new MoveInstIR(moveTempVreg, node.getSrc()));
            node.setSrc(moveTempVreg);
        }
    }

    @Override
    public void visit(LeaInstIR node) {
        if (node.getDest() instanceof MemoryIR && node.getSrc() instanceof MemoryIR) {
            node.append(new MoveInstIR(node.getDest(), moveTempVreg));
            node.dest = moveTempVreg;
        }
    }

    @Override
    public void visit(BinaryInstIR node) {
        switch (node.getOp()) {
            case SHR:
            case SHL:
                if (node.getSrc() instanceof VirtualRegisterIR) {
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
            default:
                break;
        }
    }

    @Override
    public void visit(PushInstIR node) {
        if (node.getSrc() instanceof ImmediateIR) {
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
            node.prepend(new MoveInstIR(moveTempVreg, node.getRhs()));
            node.rhs = moveTempVreg;
        }
        node.append(new JumpInstIR(node.getFalseBB()));
        node.removeFalseBB();
    }

    @Override
    public void visit(CallInstIR node) {
        if (node.getReturnValue() != null)
            node.append(new MoveInstIR(node.getReturnValue(), RegisterSet.Vrax));
        FuncIR caller = curFunc;
        FuncIR callee = node.getFunc();
        if (callee.getType() == FuncIR.Type.USER) {
            HashSet<VirtualRegisterIR> globalVar = caller.usedGlobalVar;
//            HashSet<VirtualRegisterIR> globalVar = new HashSet<>(caller.selfDefinedGlobalVar);
//            globalVar.retainAll(callee.usedGlobalVar);
            for (VirtualRegisterIR vreg : globalVar)
                node.prepend(new MoveInstIR(vreg.memory, vreg));
//            globalVar = new HashSet<>(caller.selfUsedGlobalVar);
//            globalVar.retainAll(callee.definedGlobalVar);
            for (VirtualRegisterIR vreg : globalVar)
                node.append(new MoveInstIR(vreg, vreg.memory));
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
