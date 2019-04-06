package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Type.StringType;

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

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
