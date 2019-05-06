package com.mxcomplier.Ir;

import com.mxcomplier.Ir.Instructions.EmptyInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;

import java.util.ArrayList;
import java.util.List;

public class BasicBlockIR {
    private String lable; //for Debug
    private FuncIR func;
    private InstIR head, tail;
    public List<BasicBlockIR> fronters = new ArrayList<>();
    public List<BasicBlockIR> successors = new ArrayList<>();


    public BasicBlockIR(FuncIR func, String lable){
        this.lable = lable;
        this.func = func;
        this.head = new EmptyInstIR();
        this.tail = new EmptyInstIR();
        this.head.append(this.tail);
        func.getBBList().add(this);
    }

    void addFronter(BasicBlockIR bb){
        fronters.add(bb);
    }

    void addSuccessor(BasicBlockIR bb){
        successors.add(bb);
    }

    public void append(InstIR inst){
        tail.prepend(inst);
    }

    public void prepend(InstIR inst){
        head.append(inst);
    }

    public FuncIR getFunc() {
        return func;
    }

    public InstIR getHead() {
        return head;
    }

    public InstIR getTail() {
        return tail;
    }

    public String getLable() {
        return lable;
    }

    public String getFuncLabel(){
        return func.getName() + func.getBBList().indexOf(this);
    }

    @Override
    public String toString() {
        return getFuncLabel() + '_' + lable;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
