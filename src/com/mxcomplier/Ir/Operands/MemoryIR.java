package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Ir.IRVisitor;

public class MemoryIR extends AddressIR {
    private RegisterIR base = null;
    private RegisterIR offset = null;
    private int scale = 0;
    private int num = 0;
    private ConstantIR constant = null;


    public MemoryIR(){}

    public MemoryIR(RegisterIR base){
        this.base = base;
    }

    public MemoryIR(RegisterIR base, RegisterIR offset){
        this.base = base;
        this.offset = offset;
    }

    public MemoryIR(RegisterIR base, int num){
        this.base = base;
        this.num = num;
    }

    public MemoryIR(ConstantIR constant){
        this.constant = constant;
    }


    public OperandIR getBase() {
        return base;
    }

    public OperandIR getOffset() {
        return offset;
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

    @Override
    public String toString() {
        if (scale == 0) {
            if (offset == null)
                return String.format("[%s + %d]", base, num);
            else
                return String.format("[%s + %s]", base, offset);
        }
        else {
            if (num == 0)
                return String.format("[%s + %s * %d]", base, offset, scale);
            else
                return String.format("[%s + %s*%d + %d]", base, offset, scale, num);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
