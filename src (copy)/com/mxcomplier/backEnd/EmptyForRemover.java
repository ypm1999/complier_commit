package com.mxcomplier.backEnd;

import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.MemoryIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Ir.ProgramIR;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmptyForRemover extends IRScanner {

    static public class ForBBs{
        BasicBlockIR body, expr3, cond, after;

        public ForBBs(BasicBlockIR body, BasicBlockIR cond, BasicBlockIR expr3, BasicBlockIR after){
            this.body = body;
            this.cond = cond;
            this.expr3 = expr3;
            this.after = after;
        }

        public boolean noUseInafter(LivenessAnalyzer livenessAnalyzer){
            HashSet<VirtualRegisterIR> outSet = new HashSet<>(livenessAnalyzer.getDefinedVregs().get(body));
            outSet.addAll(livenessAnalyzer.getDefinedVregs().get(cond));
            outSet.addAll(livenessAnalyzer.getDefinedVregs().get(expr3));
            HashSet<VirtualRegisterIR> acceptSet = new HashSet<>(livenessAnalyzer.getUsedVregs().get(after));
            acceptSet.addAll(livenessAnalyzer.getLiveOut().get(after));
            outSet.retainAll(acceptSet);
            return outSet.isEmpty();
        }

        boolean contains(BasicBlockIR bb){
            return bb == body || bb == cond || bb == expr3 || bb == after;
        }
        Set<BasicBlockIR> getBBSet(){
            return new HashSet<BasicBlockIR>(Arrays.asList(body, expr3, cond, after));
        }
    }
    
    private List<FuncIR> funcList;
    public EmptyForRemover(IRBuilder ir){
        funcList = ir.root.getFuncs();
    }

    public void run(){
        for (FuncIR func : funcList)
            runFunc(func);

    }

    private void runFunc(FuncIR func){
        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer();
        livenessAnalyzer.IRlevel = true;
        livenessAnalyzer.buildLiveOut(func);

        for (ForBBs bbs : func.forSet){
            if (!bbs.noUseInafter(livenessAnalyzer))
                continue;
            Set<BasicBlockIR> bbSet = bbs.getBBSet();
            bbSet.remove(bbs.after);
            boolean removeAble = true;
            for (BasicBlockIR bb : bbSet){
                for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next){
                    if (inst instanceof CallInstIR || inst instanceof ReturnInstIR)
                        removeAble = false;
                    if ((inst instanceof MoveInstIR && ((MoveInstIR) inst).getDest() instanceof MemoryIR)
                        || (inst instanceof LeaInstIR && ((MoveInstIR) inst).getDest() instanceof MemoryIR)
                        || (inst instanceof BinaryInstIR && ((BinaryInstIR) inst).getDest() instanceof MemoryIR)
                        || (inst instanceof UnaryInstIR && ((UnaryInstIR) inst).getDest() instanceof MemoryIR))
                        removeAble = false;
                    if (inst instanceof JumpInstIR && !bbs.contains(((JumpInstIR) inst).getTarget()))
                        removeAble = false;
                    if (inst instanceof CJumpInstIR && (!bbs.contains(((CJumpInstIR) inst).getTrueBB())
                                                        || !bbs.contains(((CJumpInstIR) inst).getFalseBB())))
                        removeAble = false;

                    if(!removeAble)
                        break;
                }
                if (!removeAble)
                    break;
            }
            if (removeAble){
                while(bbs.cond.getHead().next != bbs.cond.getTail())
                    bbs.cond.getHead().next.remove();
                bbs.cond.append(new JumpInstIR(bbs.after));
            }
        }
    }

}
