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

public class BlockMerger extends IRScanner {


    private boolean basic;
    public BlockMerger(boolean basic){
        this.basic = basic;
    }

    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs()){
            func.accept(this);
        }
    }

    @Override
    public void visit(FuncIR node) {
        boolean changed = true;
        while(changed) {
            changed = false;
            node.initOrderBBList();
            List<BasicBlockIR> BBLIst = new ArrayList<>(node.getBBList());
            for (BasicBlockIR bb : BBLIst) {
                if (bb.fronters.size() == 1) {
                    BasicBlockIR prevBB = bb.fronters.iterator().next();
                    InstIR lastInst = prevBB.getTail().prev;
                    if (basic && (prevBB.successors.size() > 1 || bb == node.leaveBB))
                        continue;
                    if (lastInst instanceof JumpInstIR && ((JumpInstIR) lastInst).getTarget() == bb) {
                        lastInst.remove();
                        prevBB.merge(bb);
                        node.getBBList().remove(bb);
                        if (bb == node.leaveBB)
                            node.leaveBB = prevBB;
                        changed = true;
                        break;
                    }
                }
            }
        }
        node.initOrderBBList();
    }
}
