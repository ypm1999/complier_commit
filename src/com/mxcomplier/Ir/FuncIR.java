package com.mxcomplier.Ir;

import com.mxcomplier.Ir.Instructions.CJumpInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;
import com.mxcomplier.Ir.Instructions.JumpInstIR;
import com.mxcomplier.Ir.Operands.PhysicalRegisterIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FuncIR {
    public enum Type{
        EXTRA, LIBRARY, USER
    }
    private String name;
    private Type type;
    public BasicBlockIR entryBB, leaveBB;
    public HashSet<VirtualRegisterIR> usedGlobalVar = new HashSet<>();
    private HashSet<PhysicalRegisterIR> definedPhyRegs = null, usedPhyRegs = null;
    private List<BasicBlockIR> BBList = new ArrayList<>();
    private List<BasicBlockIR> orderedBBList, reversedOrderedBBList;
    private List<FuncIR> callee = new ArrayList<>();
    private List<VirtualRegisterIR> parameters = new ArrayList<>();

    public FuncIR(String name){
        this.name = name;
        this.type = Type.USER;
    }

    public FuncIR(String name, Type type){
        this.name = name;
        this.type = type;
    }

    static private HashSet<BasicBlockIR> accessed = new HashSet<>();
    private void dfsBB(BasicBlockIR now, BasicBlockIR fa){
        if (fa != null) {
            now.addFronter(fa);
            fa.addSuccessor(now);
        }
        if (accessed.contains(now))
            return;
        accessed.add(now);
        InstIR inst = now.getTail().prev;
        if (inst instanceof JumpInstIR)
            dfsBB(((JumpInstIR) inst).getTarget(), now);
        if (inst instanceof CJumpInstIR){
            dfsBB(((CJumpInstIR) inst).getTrueBB(), now);
            dfsBB(((CJumpInstIR) inst).getFalseBB(), now);
        }
        reversedOrderedBBList.add(now);
    }

    public void initOrderBBList(){
        accessed.clear();
        reversedOrderedBBList = new ArrayList<>();
        dfsBB(entryBB, null);
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

    public List<FuncIR> getCallee() {
        return callee;
    }

    public Type getType() {
        return type;
    }

    public List<VirtualRegisterIR> getParameters() {
        return parameters;
    }

    public HashSet<PhysicalRegisterIR> getDefinedPhyRegs(){

        if (definedPhyRegs == null){
            definedPhyRegs = new HashSet<>();
            if (type == Type.LIBRARY)
                definedPhyRegs.addAll(RegisterSet.allocatePhyRegisterSet);
            else
                for (BasicBlockIR bb:BBList){
                    for(InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next){
                        for (VirtualRegisterIR vreg: inst.getUsedVReg())
                            definedPhyRegs.add(vreg.getPhyReg());
                    }
                }
        }
        return definedPhyRegs;
    }

    public HashSet<PhysicalRegisterIR> getUsedPhyRegs() {
        if (usedPhyRegs == null){
            usedPhyRegs = new HashSet<>();
            if (type == Type.LIBRARY)
                usedPhyRegs.addAll(RegisterSet.allocatePhyRegisterSet);
            else
                for (BasicBlockIR bb:BBList){
                    for(InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next){
                        for (VirtualRegisterIR vreg: inst.getUsedVReg())
                            usedPhyRegs.add(vreg.getPhyReg());
                    }
                }
        }
        return usedPhyRegs;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
