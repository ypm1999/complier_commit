package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;

public class VirtualRegisterIR extends RegisterIR {
    static private int vRegId = 0;

    private int id;
    public MemoryIR memory = null;
    private PhysicalRegisterIR phyReg = null;

    public VirtualRegisterIR(String label){
        this.id = vRegId++;
        this.lable = label;
        this.memory = new StackSoltIR(lable + "_solt");
    }

    public VirtualRegisterIR(String label, PhysicalRegisterIR phy){
        this.id = -1;
        this.lable = label;
        this.phyReg = phy;
    }

    public int getId() {
        return id;
    }

    static public int getVregId(){
        return vRegId;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public void setPhyReg(PhysicalRegisterIR phyReg) {
        this.phyReg = phyReg;
    }

    public PhysicalRegisterIR getPhyReg() {
        return phyReg;
    }

    @Override
    public String toString() {
        if (phyReg == null)
            return String.format("vReg%d_%s", id, lable);
        else
            return phyReg.toString();
    }
}
