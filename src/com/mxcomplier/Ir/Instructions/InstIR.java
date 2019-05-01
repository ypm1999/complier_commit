package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.StackSoltIR;
import com.mxcomplier.Type.StringType;

import java.util.ArrayList;
import java.util.List;

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

    public List<StackSoltIR> getStackSolt(){
        return new ArrayList<>();
    }



    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
