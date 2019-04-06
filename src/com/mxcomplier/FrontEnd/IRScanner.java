package com.mxcomplier.FrontEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Ir.ProgramIR;

abstract public class IRScanner implements IRVisitor {
    IRBuilder builder;
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
}
