package com.mxcomplier.Ir;

import com.mxcomplier.Error.IRError;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.AddressIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.PhysicalRegisterIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.backEnd.EmptyForRemover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FuncIR {

    static private int ID = 0;
    static private HashSet<BasicBlockIR> accessed = new HashSet<>();
    public VirtualRegisterIR returnValue = null;
    public BasicBlockIR entryBB, leaveBB;
    public HashSet<FuncIR> callee = new HashSet<>(), caller = new HashSet<>();
    public HashSet<VirtualRegisterIR> usedGlobalVar = new HashSet<>(), selfUsedGlobalVar = new HashSet<>();
    public HashSet<VirtualRegisterIR> definedGlobalVar = new HashSet<>(), selfDefinedGlobalVar = new HashSet<>();
    public Set<EmptyForRemover.ForBBs> forSet = new HashSet<>();
    private int id;
    private String name;
    private Type type;
    private HashSet<PhysicalRegisterIR> definedPhyRegs = null, usedPhyRegs = null;
    private List<BasicBlockIR> BBList = new ArrayList<>();
    private List<BasicBlockIR> orderedBBList, reversedOrderedBBList;
    private List<VirtualRegisterIR> parameters = new ArrayList<>();

    public FuncIR(String name) {
        this.id = ++ID;
        this.name = name;
        this.type = Type.USER;
    }

    public FuncIR(String name, Type type) {
        this.id = ++ID;
        this.name = name;
        this.type = type;
    }

    public int getInstNum() {
        int res = 0;
        for (BasicBlockIR bb : BBList) {
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                res++;
            }
        }
        return res;
    }

    public void updateCallee() {
        for (FuncIR func : callee) {
            func.caller.remove(this);
        }
        callee.clear();
        for (BasicBlockIR bb : BBList) {
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                if (inst instanceof CallInstIR) {
                    callee.add(((CallInstIR) inst).getFunc());
                    ((CallInstIR) inst).getFunc().caller.add(this);
                }
            }
        }
    }

    public HashSet<VirtualRegisterIR> getAllVreg() {
        HashSet<VirtualRegisterIR> res = new HashSet<>();
        for (BasicBlockIR bb : BBList) {
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                if (inst instanceof CallInstIR) {
                    for (OperandIR arg : ((CallInstIR) inst).getArgs())
                        res.addAll(inst.getVreg(arg));
                    if (((CallInstIR) inst).getReturnValue() != null)
                        res.addAll(inst.getVreg(((CallInstIR) inst).getReturnValue()));
                } else {
                    res.addAll(inst.getUsedVReg());
                    res.addAll(inst.getDefinedVreg());
                }
            }
        }
        return res;
    }

    public void initGlobalDefined() {
        HashSet<VirtualRegisterIR> usedVreg = new HashSet<>(), definedVreg = new HashSet<>();
        for (BasicBlockIR bb : BBList) {
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                if (inst instanceof CallInstIR) {
                    for (OperandIR arg : ((CallInstIR) inst).getArgs())
                        usedVreg.addAll(inst.getVreg(arg));
                    if (((CallInstIR) inst).getReturnValue() != null) {
                        AddressIR ret = ((CallInstIR) inst).getReturnValue();
                        if (ret instanceof VirtualRegisterIR)
                            definedVreg.add((VirtualRegisterIR) ret);
                        else
                            usedVreg.addAll(inst.getVreg(ret));
                    }
                    continue;
                }
                usedVreg.addAll(inst.getUsedVReg());
                definedVreg.addAll(inst.getDefinedVreg());
            }
        }

        definedGlobalVar = new HashSet<>(usedGlobalVar);
        usedGlobalVar.retainAll(usedVreg);
        definedGlobalVar.retainAll(definedVreg);

        selfUsedGlobalVar = new HashSet<>(usedGlobalVar);
        selfDefinedGlobalVar = new HashSet<>(definedGlobalVar);
    }

    private void reverseOrderDfsBB(BasicBlockIR now, BasicBlockIR fa) {
        if (fa != null) {
            now.addFronter(fa);
            fa.addSuccessor(now);
        }
        if (accessed.contains(now))
            return;
        accessed.add(now);

        for (InstIR inst = now.getTail().prev; inst != now.getHead(); inst = inst.prev) {
            if (inst instanceof JumpInstIR)
                reverseOrderDfsBB(((JumpInstIR) inst).getTarget(), now);
            if (inst instanceof CJumpInstIR) {
                reverseOrderDfsBB(((CJumpInstIR) inst).getTrueBB(), now);
                if (((CJumpInstIR) inst).getFalseBB() != null)
                    reverseOrderDfsBB(((CJumpInstIR) inst).getFalseBB(), now);
            }
        }
        reversedOrderedBBList.add(now);
    }

    private void orderDfsBB(BasicBlockIR now, BasicBlockIR fa) {
        if (fa != null) {
            now.addFronter(fa);
            fa.addSuccessor(now);
        }
        if (accessed.contains(now))
            return;
        accessed.add(now);
        orderedBBList.add(now);
        for (InstIR inst = now.getTail().prev; inst != now.getHead(); inst = inst.prev) {
            if (inst instanceof EmptyInstIR)
                throw new IRError("empty");
            if (inst instanceof JumpInstIR)
                orderDfsBB(((JumpInstIR) inst).getTarget(), now);
            if (inst instanceof CJumpInstIR) {
                orderDfsBB(((CJumpInstIR) inst).getTrueBB(), now);
                if (((CJumpInstIR) inst).getFalseBB() != null)
                    orderDfsBB(((CJumpInstIR) inst).getFalseBB(), now);
            }
        }
    }

    public void initReverseOrderBBList() {
        for (BasicBlockIR bb : BBList)
            bb.initFrontAndSucc();
        accessed.clear();
        reversedOrderedBBList = new ArrayList<>();
        reverseOrderDfsBB(entryBB, null);
        List<BasicBlockIR> tempBBList = new ArrayList<>(BBList);
        for (BasicBlockIR bb : tempBBList) {
            if (!accessed.contains(bb))
                BBList.remove(bb);
        }
    }

    public void initOrderBBList() {
        for (BasicBlockIR bb : BBList)
            bb.initFrontAndSucc();
        accessed.clear();
        orderedBBList = new ArrayList<>();
        orderDfsBB(entryBB, null);
        List<BasicBlockIR> tempBBList = new ArrayList<>(BBList);
        for (BasicBlockIR bb : tempBBList) {
            if (!accessed.contains(bb))
                BBList.remove(bb);
        }
    }

    public List<BasicBlockIR> getReversedOrderedBBList() {
        return reversedOrderedBBList;
    }

    public List<BasicBlockIR> getOrderedBBList() {
        return orderedBBList;
    }

    public String getName() {
        return name;
    }

    public List<BasicBlockIR> getBBList() {
        return BBList;
    }

    public void setBBList(List<BasicBlockIR> BBList) {
        this.BBList = BBList;
    }

    public Type getType() {
        return type;
    }

    public VirtualRegisterIR getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(VirtualRegisterIR returnValue) {
        this.returnValue = returnValue;
    }

    public BasicBlockIR getEntryBB() {
        return entryBB;
    }

    public BasicBlockIR getLeaveBB() {
        return leaveBB;
    }

    public HashSet<FuncIR> getCallee() {
        return callee;
    }

    public HashSet<FuncIR> getCaller() {
        return caller;
    }

    public HashSet<VirtualRegisterIR> getDefinedGlobalVar() {
        return definedGlobalVar;
    }

    public HashSet<VirtualRegisterIR> getUsedGlobalVar() {
        return usedGlobalVar;
    }

    public HashSet<VirtualRegisterIR> getSelfDefinedGlobalVar() {
        return selfDefinedGlobalVar;
    }

    public HashSet<VirtualRegisterIR> getSelfUsedGlobalVar() {
        return selfUsedGlobalVar;
    }

    public List<VirtualRegisterIR> getParameters() {
        return parameters;
    }

    public HashSet<PhysicalRegisterIR> getDefinedPhyRegs() {
        if (definedPhyRegs == null) {
            definedPhyRegs = new HashSet<>();
            if (type == Type.LIBRARY)
                definedPhyRegs.addAll(RegisterSet.allocatePhyRegisterSet);
            else {
                for (BasicBlockIR bb : BBList) {
                    for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                        for (VirtualRegisterIR vreg : inst.getDefinedVreg())
                            definedPhyRegs.add(vreg.getPhyReg());
                    }
                }
            }
        }
        return definedPhyRegs;
    }

    public HashSet<PhysicalRegisterIR> getUsedPhyRegs() {
        if (usedPhyRegs == null) {
            usedPhyRegs = new HashSet<>();
            if (type == Type.LIBRARY)
                usedPhyRegs.addAll(RegisterSet.allocatePhyRegisterSet);
            else
                for (BasicBlockIR bb : BBList) {
                    for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                        for (VirtualRegisterIR vreg : inst.getUsedVReg())
                            usedPhyRegs.add(vreg.getPhyReg());
                    }
                }
        }
        return usedPhyRegs;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public enum Type {
        EXTRA, LIBRARY, USER
    }
}
