package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;

public class VirtualRegisterIR extends RegisterIR {
    static private int vRegId = 0;
    private MemoryIR memory = null;
    private boolean tempVar = false;
    private VirtualRegisterIR alias = null;
    private int id;
    private PhysicalRegisterIR phyReg = null;

    public VirtualRegisterIR getAlias() {
        return alias;
    }

    public MemoryIR getMemory() {
        return memory;
    }

    public boolean isTempVar() {
        return tempVar;
    }

    public void setAlias(VirtualRegisterIR alias) {
        this.alias = alias;
    }

    public void setMemory(MemoryIR memory) {
        this.memory = memory;
    }

    public void setTempVar(boolean tempVar) {
        this.tempVar = tempVar;
    }

    public VirtualRegisterIR(String label) {
        this.id = vRegId++;
        this.lable = label;
    }

    public VirtualRegisterIR(VirtualRegisterIR other) {
        this.id = vRegId++;
        this.lable = other.lable + "_cp";
        this.tempVar = other.tempVar;
        this.memory = other.memory;
        this.phyReg = other.phyReg;
        this.alias = other.alias;
    }

    public VirtualRegisterIR(String label, PhysicalRegisterIR phy) {
        this.id = -1;
        this.lable = label;
        this.phyReg = phy;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public PhysicalRegisterIR getPhyReg() {
        return phyReg;
    }

    public void setPhyReg(PhysicalRegisterIR phyReg) {
        this.phyReg = phyReg;
    }

    @Override
    public VirtualRegisterIR copy() {
        return this;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        if (phyReg == null)
            return String.format("vReg%d_%s", id, lable);
        else
            return phyReg.toString();
    }
}
