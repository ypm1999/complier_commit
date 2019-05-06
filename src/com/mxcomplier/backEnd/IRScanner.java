package com.mxcomplier.backEnd;

import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.*;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.*;

abstract public class IRScanner implements IRVisitor {
    IRBuilder builder;



    MemoryIR getMemory(OperandIR operand){
        if (operand instanceof VirtualRegisterIR)
            return ((VirtualRegisterIR) operand).memory;
        else if (operand instanceof MemoryIR)
            return (MemoryIR) operand;
        else
            return null;
    }

    MemoryIR getVregMemory(OperandIR operand){
        if (operand instanceof VirtualRegisterIR)
            return ((VirtualRegisterIR) operand).memory;
        else
            return null;
    }

    void fixMemory(MemoryIR mem, InstIR node){
        if (mem == null)
            return;

        MemoryIR base = getMemory(mem.getBase());
        MemoryIR offset = getMemory(mem.getOffset());
        if (mem.old_base != null || mem.old_offset != null){
            base = getMemory(mem.old_base);
            offset = getMemory(mem.old_offset);
        }
        else{
            mem.old_base = mem.getBase();
            mem.old_offset = mem.getOffset();
        }
        fixMemory(base, node);
        fixMemory(offset, node);
        if (base != null){
            node.prepend(new MoveInstIR(RegisterSet.r8, base));
            mem.setBase(RegisterSet.Vr8);
        }
        if (mem.getOffset() != null){
            node.prepend(new MoveInstIR(RegisterSet.r9, offset));
            mem.setOffset(RegisterSet.Vr9);
        }
    }

    @Override
    public void visit(BasicBlockIR node) {

    }

    @Override
    public void visit(ProgramIR node) {

    }

    @Override
    public void visit(FuncIR node) {

    }

    @Override
    public void visit(InstIR node) {

    }


    @Override
    public void visit(CallInstIR node) {

    }

    @Override
    public void visit(UnaryInstIR node) {

    }

    @Override
    public void visit(JumpInstIR node) {

    }

    @Override
    public void visit(CJumpInstIR node) {

    }

    @Override
    public void visit(BinaryInstIR node) {

    }

    @Override
    public void visit(MoveInstIR node) {

    }

    @Override
    public void visit(PopInstIR node) {

    }

    @Override
    public void visit(PushInstIR node) {

    }

    @Override
    public void visit(ReturnInstIR node) {

    }

    @Override
    public void visit(CompInstIR node) {

    }

    @Override
    public void visit(AddressIR node) {

    }

    @Override
    public void visit(ConstantIR node) {

    }

    @Override
    public void visit(FuncAddressIR node) {

    }

    @Override
    public void visit(VirtualRegisterIR node) {

    }

    @Override
    public void visit(PhysicalRegisterIR node) {

    }

    @Override
    public void visit(ImmediateIR node) {

    }

    @Override
    public void visit(StaticDataIR node) {

    }

    @Override
    public void visit(BranchInstIR node) {

    }

    @Override
    public void visit(LeaInstIR node) {

    }
}
