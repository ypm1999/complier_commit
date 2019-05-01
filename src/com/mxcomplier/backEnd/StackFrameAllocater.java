package com.mxcomplier.backEnd;

import com.mxcomplier.Config;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Ir.ProgramIR;
import com.mxcomplier.Ir.RegisterSet;

import javax.swing.plaf.metal.MetalMenuBarUI;
import java.util.*;

import static java.lang.Integer.max;
import static java.lang.Math.min;

public class StackFrameAllocater extends IRScanner {

    private class Frame{
        public List<AddressIR> tempVar = new ArrayList<>();
        public AddressIR old_rbp = new StackSoltIR("old_rbp");
    }

    Frame curFrame = null;

    static PhysicalRegisterIR[] paratReg = {
            RegisterSet.rdi,
            RegisterSet.rsi,
            RegisterSet.rdx,
            RegisterSet.rcx,
            RegisterSet.r8,
            RegisterSet.r9
    };


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
            func.accept(this);
        }
    }

    @Override
    public void visit(FuncIR node) {
        InstIR firstInst = node.entryBB.getHead().next;
        firstInst.prepend(new PushInstIR(RegisterSet.rbp));
        firstInst.prepend(new MoveInstIR(RegisterSet.rbp, RegisterSet.rsp));

        HashSet<StackSoltIR> stackSolts = new HashSet<>();
        for(BasicBlockIR bb: node.getBBList()){
            InstIR inst = bb.getHead().next;
            while(inst != bb.getTail()){
                stackSolts.addAll(inst.getStackSolt());
                inst = inst.next;
            }
        }
        Iterator<VirtualRegisterIR> args = node.getParameters().iterator();
        int cnt = 1;
        while(args.hasNext()){
            MemoryIR tmp = getVregMemory(args.next());
            assert(tmp instanceof StackSoltIR);
            stackSolts.remove(tmp);
            tmp.setNum(Config.getREGSIZE() * (++cnt));
        }

        int i = 0;
        for (StackSoltIR stackSolt: stackSolts){
            stackSolt.setNum(-Config.getREGSIZE() * (++i));
        }

        firstInst.prepend(new BinaryInstIR(BinaryInstIR.Op.SUB, RegisterSet.rsp,
                              new ImmediateIR((stackSolts.size() + 1) * Config.getREGSIZE())));

        for (BasicBlockIR bb : node.getBBList()){
            bb.accept(this);
        }
    }

    @Override
    public void visit(CallInstIR node) {
        List<OperandIR> args = node.getArgs();
        int cnt = min(node.getArgs().size() - 1, 5);

        for (int i = args.size()-1; i >= 0; i--){
            OperandIR arg = args.get(i);
            if (args.size() - i < 6) {
                if (arg instanceof ImmediateIR)
                    node.prepend(new MoveInstIR(paratReg[cnt], arg));
                else {
                    MemoryIR mem = getMemory(arg);
                    fixMemory(mem, node);
                    node.prepend(new MoveInstIR(paratReg[cnt], mem));
                }
                node.prepend(new PushInstIR(paratReg[cnt]));
                cnt--;
            }
            else {
                if (arg instanceof ImmediateIR)
                    node.prepend(new MoveInstIR(RegisterSet.rax, arg));
                else {
                    MemoryIR mem = getMemory(arg);
                    fixMemory(mem, node);
                    node.prepend(new MoveInstIR(RegisterSet.rax, mem));
                }
                node.prepend(new PushInstIR(RegisterSet.rax));
            }
        }
        if (node.getArgs().size() > 0)
            node.append(new BinaryInstIR(BinaryInstIR.Op.ADD, RegisterSet.rsp,
                                        new ImmediateIR(Config.getREGSIZE()*node.getArgs().size())));
        if (node.getReturnValue() != null)
            node.append(new MoveInstIR(getMemory(node.getReturnValue()), RegisterSet.rax));
    }
}

