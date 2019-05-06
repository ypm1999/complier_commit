package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.*;
import com.mxcomplier.Type.StringType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract public class InstIR {
    public InstIR prev, next;

    public InstIR(){
        prev = next = null;
    }

    public InstIR(InstIR prev, InstIR next){
        this.prev = prev;
        this.next = next;
    }

    public void prepend(InstIR inst){
        inst.prev = null;
        if (this.prev != null){
            inst.prev = this.prev;
            this.prev.next = inst;
        }
        this.prev = inst;
        inst.next = this;
    }

    public void append(InstIR inst){
        inst.next = null;
        if (this.next != null){
            inst.next = this.next;
            this.next.prev = inst;
        }
        this.next = inst;
        inst.prev = this;
    }

    public void remove(){
        this.next.prev = this.prev;
        this.prev.next = this.next;
    }

    public List<StackSoltIR> getStackSolt(){
        return new ArrayList<>();
    }


    public List<VirtualRegisterIR> getUsedVReg(){
        return new ArrayList<>();
    }

    public List<VirtualRegisterIR> getDefinedVreg(){
        return new ArrayList<>();
    }

    public void replaceVreg(Map<VirtualRegisterIR, VirtualRegisterIR> renameMap){}

    OperandIR replacedVreg(OperandIR reg, Map<VirtualRegisterIR, VirtualRegisterIR> renameMap){
        if (reg == null)
            return null;
        if (reg instanceof VirtualRegisterIR && renameMap.containsKey(reg))
            return renameMap.get(reg);
        if (reg instanceof MemoryIR){
            MemoryIR mem = (MemoryIR) reg;
            mem.setBase((VirtualRegisterIR) replacedVreg(mem.getBase(), renameMap));
            mem.setOffset((VirtualRegisterIR) replacedVreg(mem.getOffset(), renameMap));
        }
        return reg;
    }


    public List<VirtualRegisterIR> getVreg(OperandIR oper){
        List<VirtualRegisterIR> regs = new ArrayList<>();
        if (oper instanceof VirtualRegisterIR)
            regs.add((VirtualRegisterIR) oper);
        else if (oper instanceof MemoryIR)
            return ((MemoryIR) oper).getVreg();
        return regs;
    }


    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
