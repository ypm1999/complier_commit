package com.mxcomplier.backEnd;

import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.BranchInstIR;
import com.mxcomplier.Ir.Instructions.CJumpInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.JumpInstIR;
import com.mxcomplier.Ir.ProgramIR;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BlockCopier extends IRScanner {

    static final private int MAX_COPY_INST_NUM = 8;
    static final private int CJUMP_MAX_COPY_INST_NUM = 4;
    static final private int CJUMP_MAX_BB_INST_NUM = 16;
    static final private int MAX_TOTAL_COPY_INST_NUM = 64;

    private boolean basic;

    public BlockCopier(boolean basic) {
        this.basic = basic;
    }

    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs()) {
            func.accept(this);
        }
    }

    @Override
    public void visit(FuncIR node) {
        boolean changed = true;
        while (changed) {
            changed = false;
            node.initOrderBBList();
            List<BasicBlockIR> BBList = node.getOrderedBBList();
            for (BasicBlockIR bb : BBList) {
                if (bb.fronters.isEmpty() || (!basic && bb.getInstNum() > CJUMP_MAX_BB_INST_NUM))
                    continue;
                InstIR lastInst = bb.getTail().prev;
                if (lastInst instanceof JumpInstIR){
                    BasicBlockIR nextBB = ((JumpInstIR) lastInst).getTarget();
                    if (nextBB == bb || nextBB == node.leaveBB)
                        continue;
                    int instNum = nextBB.getInstNum();
                    if (instNum < (basic ? MAX_COPY_INST_NUM : CJUMP_MAX_COPY_INST_NUM)
                            && nextBB.fronters.size() * instNum < MAX_TOTAL_COPY_INST_NUM){
                        lastInst.remove();
                        bb.merge(nextBB.copy());
                        nextBB.fronters.remove(bb);
                        changed = true;
                    }
                }
            }
        }
        node.initReverseOrderBBList();
    }
}
