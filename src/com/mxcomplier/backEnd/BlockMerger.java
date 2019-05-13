package com.mxcomplier.backEnd;

import com.mxcomplier.Error.IRError;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.BranchInstIR;
import com.mxcomplier.Ir.Instructions.CJumpInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.JumpInstIR;
import com.mxcomplier.Ir.ProgramIR;

import java.util.*;

public class BlockMerger extends IRScanner {


    private boolean basic;

    public BlockMerger(boolean basic) {
        this.basic = basic;
    }

    void runBasic(){

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
//                System.err.println(bb);
                InstIR lastInst = bb.getTail().prev;
                if (lastInst instanceof CJumpInstIR){
                    if (((CJumpInstIR) lastInst).getTrueBB() == ((CJumpInstIR) lastInst).getFalseBB()){
                        lastInst.append(new JumpInstIR(((CJumpInstIR) lastInst).getTrueBB()));
                        lastInst.remove();
//                        System.err.println("case1");
                        changed = true;
                    }
                }
                if (lastInst instanceof JumpInstIR){
                    BasicBlockIR nextBB = ((JumpInstIR) lastInst).getTarget();
                    if (nextBB == bb || removedBB.contains(nextBB))
                        continue;
                    if (bb.getInstNum() == 1){
                        HashMap<BasicBlockIR, BasicBlockIR> renameMap = new HashMap<>();
                        renameMap.put(bb, nextBB);
                        for (BasicBlockIR prevBB: bb.fronters) {
                            if (removedBB.contains(prevBB))
                                continue;
//                            System.err.println(prevBB + " <- " + bb);
                            ((BranchInstIR) prevBB.getTail().prev).bbRename(renameMap);
                            changed = true;
                        }
//                        System.err.println("case2");
                        break;
                    }
                    if (nextBB.fronters.size() == 1 && nextBB.getInstNum() > 1) {
//                        System.err.println(bb + " <--- " + nextBB);
                    lastInst.remove();
                    bb.merge(nextBB);
                    removedBB.add(nextBB);
                    if (nextBB == node.leaveBB)
                        node.leaveBB = bb;
//                    System.err.println("case3");
                    changed = true;
                }
                }

            }
//            System.err.println("finished");
//                if (bb.fronters.size() == 1) {
//                    BasicBlockIR prevBB = bb.fronters.iterator().next();
//                    InstIR lastInst = prevBB.getTail().prev;
//                    if (basic && (prevBB.successors.size() > 1 || bb == node.leaveBB))
//                        continue;
//                    if (lastInst instanceof JumpInstIR && ((JumpInstIR) lastInst).getTarget() == bb) {
//                        lastInst.remove();
//                        prevBB.merge(bb);
//                        node.getBBList().remove(bb);
//                        if (bb == node.leaveBB)
//                            node.leaveBB = prevBB;
//                        changed = true;
//                        break;
//                    }
//                }
//            }
        }
    }
}
