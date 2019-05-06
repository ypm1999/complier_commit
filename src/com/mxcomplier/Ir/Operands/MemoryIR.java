package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.RegisterSet;

import java.util.ArrayList;
import java.util.List;

public class MemoryIR extends AddressIR {
    private VirtualRegisterIR base = null;
    private VirtualRegisterIR offset = null;
    public VirtualRegisterIR old_base = null;
    public VirtualRegisterIR old_offset = null;
    private int scale = 1;
    private int num = 0;
    private ConstantIR constant = null;


    public MemoryIR(){}

    public MemoryIR(VirtualRegisterIR base){
        this.base = base;
    }

    public MemoryIR(VirtualRegisterIR base, VirtualRegisterIR offset){
        this.base = base;
        this.offset = offset;
    }

    public MemoryIR(VirtualRegisterIR base, VirtualRegisterIR offset, int scale){
        this.base = base;
        this.offset = offset;
        this.scale = scale;
    }

    public MemoryIR(VirtualRegisterIR base, int num){
        this.base = base;
        this.num = num;
    }

    public MemoryIR(ConstantIR constant){
        this.constant = constant;
    }


    public VirtualRegisterIR getBase() {
        return base;
    }

    public VirtualRegisterIR getOffset() {
        return offset;
    }

    public void setBase(VirtualRegisterIR base) {
        this.base = base;
    }

    public void setOffset(VirtualRegisterIR offset) {
        this.offset = offset;
    }

    public ConstantIR getConstant() {
        return constant;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<VirtualRegisterIR> getVreg(){
        List<VirtualRegisterIR> regs = new ArrayList<>();
        if (base != null)
            regs.add(base);
        if (offset != null)
            regs.add(offset);
        return regs;
    }

    @Override
    public String toString() {
        if (constant != null)
            return "[rel " + constant.lable + "]";
        String str1, str2, str3;
        str1 = "" + base;
        if (offset == null)
            str2 = "";
        else{
            if (scale == 1)
                str2 = " + " + offset.toString();
            else
                str2 = " + " + offset.toString() + "*" + scale;
        }
        if (num > 0)
            str3 = " + " + num;
        else if (num < 0)
            str3 = " - " + (-num);
        else
            str3 = "";
        return "qword [" + str1 + str2 + str3 + ']';
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
