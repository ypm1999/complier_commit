package com.mxcomplier.Ir.Operands;

import com.mxcomplier.Config;
import com.mxcomplier.Ir.IRVisitor;

public class StaticDataIR extends ConstantIR {
    private int size;
    private String constString = null;

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

    @Override
    public String toString() {
        if (constString == null){
            StringBuilder tmp = new StringBuilder("db");
            for (int i = 0; i < size; i++)
                tmp.append(" 00H,");
            return tmp.substring(0, tmp.length() - 1);
        }
        else{
            StringBuilder tmp = new StringBuilder(String.format("dq %d\ndb ", constString.length()));
            for (int i = 0; i < constString.length(); ++i)
                tmp.append(String.format("%02XH, ", (int) constString.charAt(i)));
            return tmp.toString() + "00H";
        }

    }

    public String nasmString() {
        return  toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
