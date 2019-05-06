package com.mxcomplier.backEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Ir.ProgramIR;
import com.mxcomplier.Ir.RegisterSet;

public class RegisterAllocator extends IRScanner {

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
        int cnt = 0;
        for (StaticDataIR mem : node.getStaticData()){
            mem.lable = "__Static" + cnt + "_" + mem.lable;
            cnt++;
        }
        for (FuncIR func : node.getFuncs()){
            func.accept(this);
        }
    }

    @Override
    public void visit(FuncIR node) {
        for (BasicBlockIR bb : node.getBBList()){
            bb.accept(this);
        }
    }

    @Override
    public void visit(CallInstIR node) {

    }

    @Override
    public void visit(UnaryInstIR node) {
        MemoryIR dest = getMemory(node.dest);
        fixMemory(dest, node);

        assert(dest != null);
        node.prepend(new MoveInstIR(RegisterSet.rax, dest));
        node.dest = RegisterSet.rax;
        node.append(new MoveInstIR(dest, RegisterSet.rax));
    }

    @Override
    public void visit(JumpInstIR node) {

    }

    @Override
    public void visit(CJumpInstIR node) {
        MemoryIR lhs = getMemory(node.getLhs());
        MemoryIR rhs = getMemory(node.getRhs());
        CompInstIR cmp = new CompInstIR(node.getLhs(), node.getRhs());
        node.prepend(cmp);
        fixMemory(lhs, cmp);
        if (lhs != null) {
            cmp.prepend(new MoveInstIR(RegisterSet.rcx, lhs));
            cmp.lhs = RegisterSet.rcx;
//            cmp.append(new MoveInstIR(lhs, RegisterSet.rax));
        }
        else{
            cmp.prepend(new MoveInstIR(RegisterSet.rcx, node.getLhs()));
            cmp.lhs = RegisterSet.rcx;
        }
        fixMemory(rhs, cmp);
        if (rhs != null) {
            cmp.prepend(new MoveInstIR(RegisterSet.rdx, rhs));
            cmp.rhs = RegisterSet.rdx;
//            cmp.append(new MoveInstIR(rhs, RegisterSet.rdx));
        }
    }

    @Override
    public void visit(BinaryInstIR node) {
        MemoryIR dest = getMemory(node.getDest());
        MemoryIR src = getMemory(node.getSrc());
        fixMemory(dest, node);

        if (dest != null) {
            node.prepend(new MoveInstIR(RegisterSet.rax, dest));
            node.dest = RegisterSet.rax;
            if (node.getOp() == BinaryInstIR.Op.MOD)
                node.append(new MoveInstIR(dest, RegisterSet.rdx));
            else
                node.append(new MoveInstIR(dest, RegisterSet.rax));
        }
        fixMemory(src, node);
        if (src != null) {
            node.prepend(new MoveInstIR(RegisterSet.rbx, src));
            node.src = RegisterSet.rbx;
        }
        else{
            switch (node.getOp()){
                case MUL:
                case DIV:
                case MOD:
                    node.prepend(new MoveInstIR(RegisterSet.rbx, node.getSrc()));
                    node.src = RegisterSet.rbx;
                    break;
                default: break;
            }
        }
    }

    @Override
    public void visit(MoveInstIR node) {
        MemoryIR dest = getMemory(node.getDest());
        MemoryIR src = getMemory(node.getSrc());
        fixMemory(dest, node);

        if (dest != null) {
//            node.prepend(new MoveInstIR(RegisterSet.rax, dest));
            node.dest = dest;
//            node.append(new MoveInstIR(dest, RegisterSet.rax));
        }
        fixMemory(src, node);
        if (src != null) {
            node.prepend(new MoveInstIR(RegisterSet.rdx, src));
            node.src = RegisterSet.rdx;
//            node.append(new MoveInstIR(src, RegisterSet.rdx));
        }
        else if (node.getSrc() instanceof ImmediateIR){
            node.prepend(new MoveInstIR(RegisterSet.rdx, node.getSrc()));
            node.src = RegisterSet.rdx;
        }
    }

    @Override
    public void visit(LeaInstIR node) {
        MemoryIR dest = getMemory(node.getDest());
        fixMemory(dest, node);
        fixMemory((MemoryIR) node.getSrc(), node);
        if (dest != null) {
            node.dest = RegisterSet.rdi;
            node.append(new MoveInstIR(dest, RegisterSet.rdi));
        }
//        if (src != null) {
//            node.prepend(new MoveInstIR(RegisterSet.rsi, src));
//            node.src = RegisterSet.rsi;
//        }
    }

    @Override
    public void visit(PopInstIR node) {

    }

    @Override
    public void visit(PushInstIR node) {

    }

    @Override
    public void visit(ReturnInstIR node) {
        if (node.getSrc() != null)
            node.prepend(new MoveInstIR(RegisterSet.rax, getVregMemory(node.getSrc())));
    }


}
