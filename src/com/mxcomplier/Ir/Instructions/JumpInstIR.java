package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.IRVisitor;

import java.util.Map;

public class JumpInstIR extends BranchInstIR {
    private BasicBlockIR target;

    public JumpInstIR(BasicBlockIR target){
        this.target = target;
    }

    public BasicBlockIR getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return String.format("jmp %s", target);
    }

    public String nasmString() {
        return toString();
    }

    @Override
    public void bbRename(Map<BasicBlockIR, BasicBlockIR> bbRenameMap) {
        target = bbRenameMap.getOrDefault(target, target);
    }

    @Override
    public InstIR copy() {
        return new JumpInstIR(target);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
