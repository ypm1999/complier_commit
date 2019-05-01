package com.mxcomplier.Ir;

import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.ArrayList;
import java.util.List;

public class FuncIR {
    public enum Type{
        EXTRA, LIBRARY, USER
    }
    private String name;
    private Type type;
    public BasicBlockIR entryBB, leaveBB;
    private List<BasicBlockIR> BBList = new ArrayList<>();
    private List<FuncIR> callee = new ArrayList<>();
    private List<VirtualRegisterIR> parameters = new ArrayList<>();

    public FuncIR(String name){
        this.name = name;
        this.type = Type.USER;
    }

    public FuncIR(String name, Type type){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public List<BasicBlockIR> getBBList() {
        return BBList;
    }

    public List<FuncIR> getCallee() {
        return callee;
    }

    public Type getType() {
        return type;
    }

    public List<VirtualRegisterIR> getParameters() {
        return parameters;
    }


    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
