package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Config;
import com.mxcomplier.Ir.IRVisitor;

public class StaticDataIR extends ConstantIR {
    private int size;
    private String constString;

    public StaticDataIR(){
        this.size = Config.getREGSIZE();
    }

    public StaticDataIR(String constString){
        this.constString = constString;
        this.size = constString.length() + 1 + Config.getREGSIZE();
    }

    public int getSize() {
        return size;
    }

    public String getConstString() {
        return constString;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
