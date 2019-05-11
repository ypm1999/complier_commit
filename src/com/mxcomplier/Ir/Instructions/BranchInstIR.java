package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.IRVisitor;

import java.util.Map;

abstract public class BranchInstIR extends InstIR {

    abstract public void bbRename(Map<BasicBlockIR, BasicBlockIR> bbRenameMap);

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
