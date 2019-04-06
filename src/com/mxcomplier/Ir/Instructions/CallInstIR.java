package com.mxcomplier.Ir.Instructions;

import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.IRVisitor;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.RegisterIR;

import java.util.List;

public class CallInstIR extends InstIR {

    private FuncIR func;
    private List<OperandIR> args;
    private RegisterIR returnValue;

    public CallInstIR(FuncIR func, List<OperandIR> args, RegisterIR returnValue){
        this.func = func;
        this.args = args;
        this.returnValue = returnValue;
    }

    public FuncIR getFunc() {
        return func;
    }

    public List<OperandIR> getArgs() {
        return args;
    }

    public RegisterIR getReturnValue() {
        return returnValue;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("call " + func.getName() + " (");
        for (OperandIR arg : args){
            str.append(arg).append(",");
        }
        if (!args.isEmpty())
            str.setCharAt(str.length() - 1, ')');
        else
            str.append(')');
        return str.toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
