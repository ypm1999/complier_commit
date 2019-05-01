package com.mxcomplier.Ir;

import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Type.BoolType;

public interface IRVisitor {
    void visit(BasicBlockIR node);
    void visit(ProgramIR node);
    void visit(FuncIR node);

    void visit(InstIR node);
    void visit(BranchInstIR node);
    void visit(CallInstIR node);
    void visit(UnaryInstIR node);
    void visit(JumpInstIR node);
    void visit(CJumpInstIR node);
    void visit(BinaryInstIR node);
    void visit(MoveInstIR node);
    void visit(PopInstIR node);
    void visit(PushInstIR node);
    void visit(ReturnInstIR node);
    void visit(CompInstIR node);
    void visit(LeaInstIR node);

    void visit(AddressIR node);
    void visit(ConstantIR node);
    void visit(FuncAddressIR node);
    void visit(VirtualRegisterIR node);
    void visit(PhysicalRegisterIR node);
    void visit(ImmediateIR node);
    void visit(StaticDataIR node);
}
