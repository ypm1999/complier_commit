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

public class BlockMerger extends IRScanner {


    private boolean basic;

    public BlockMerger(boolean basic) {
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
            node.initReverseOrderBBList();
            List<BasicBlockIR> BBList = node.getReversedOrderedBBList();
            HashSet<BasicBlockIR> removedBB = new HashSet<>();
            for (BasicBlockIR bb : BBList) {
                if (removedBB.contains(bb))
                    continue;
                InstIR lastInst = bb.getTail().prev;
                if (lastInst instanceof CJumpInstIR) {
                    if (((CJumpInstIR) lastInst).getTrueBB() == ((CJumpInstIR) lastInst).getFalseBB()) {
                        lastInst.append(new JumpInstIR(((CJumpInstIR) lastInst).getTrueBB()));
                        lastInst.remove();
                        changed = true;
                    }
                }
                if (lastInst instanceof JumpInstIR) {
                    BasicBlockIR nextBB = ((JumpInstIR) lastInst).getTarget();
                    if (nextBB == bb || removedBB.contains(nextBB))
                        continue;
                    if (bb.getInstNum() == 1) {
                        HashMap<BasicBlockIR, BasicBlockIR> renameMap = new HashMap<>();
                        renameMap.put(bb, nextBB);
                        for (BasicBlockIR prevBB : bb.getFronters()) {
                            if (removedBB.contains(prevBB))
                                continue;
                            ((BranchInstIR) prevBB.getTail().prev).bbRename(renameMap);
                            changed = true;
                        }
                        break;
                    }
                    if (nextBB.getFronters().size() == 1 && nextBB.getInstNum() > 1) {
                        lastInst.remove();
                        bb.merge(nextBB);
                        removedBB.add(nextBB);
                        if (nextBB == node.leaveBB)
                            node.leaveBB = bb;
                        changed = true;
                    }
                    if (nextBB.getInstNum() == 1 && nextBB.getTail().prev instanceof CJumpInstIR) {
//                        System.err.println(bb + " <- " + nextBB);
                        CJumpInstIR inst = (CJumpInstIR) nextBB.getTail().prev;
                        lastInst.append(inst.copy());
                        lastInst.remove();
                        changed = true;
                    }
                }
            }
        }
        node.initReverseOrderBBList();
    }
}
