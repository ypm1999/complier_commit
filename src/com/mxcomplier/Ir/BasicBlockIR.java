package com.mxcomplier.Ir;

import com.mxcomplier.Config;
import com.mxcomplier.Ir.Instructions.EmptyInstIR;
import com.mxcomplier.Ir.Instructions.InstIR;

import java.util.ArrayList;
import java.util.List;

public class BasicBlockIR {
    private static int BBid = 0;
    private int id;

    private String lable; //for Debug
    private FuncIR func;
    private InstIR head, tail;
    public List<BasicBlockIR> fronters;
    public List<BasicBlockIR> successors;


    public BasicBlockIR(FuncIR func, String lable) {
        this.id = ++BBid;
        this.lable = lable;
        this.func = func;
        this.head = new EmptyInstIR();
        this.tail = new EmptyInstIR();
        this.head.append(this.tail);
        func.getBBList().add(this);
    }

    public BasicBlockIR copy(){
        //TODO
        BasicBlockIR newBB = new BasicBlockIR(func, lable);
        for (InstIR inst = head.next; inst != tail; inst = inst.next)
            newBB.append(inst.copy());
        return newBB;
    }

    void initFrontAndSucc() {
        fronters = new ArrayList<>();
        successors = new ArrayList<>();
    }

    public int getInstNum(){
        int res = 0;
        for (InstIR inst = head.next; inst != tail; inst = inst.next)
            res++;
        return  res;
    }

    void addFronter(BasicBlockIR bb) {
        fronters.add(bb);
    }

    void addSuccessor(BasicBlockIR bb) {
        successors.add(bb);
    }

    public void append(InstIR inst) {
        tail.prepend(inst);
    }

    public void prepend(InstIR inst) {
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

    public void setHead(InstIR head) {
        this.head = head;
    }

    public void setTail(InstIR tail) {
        this.tail = tail;
    }

    public String getLable() {
        return lable;
    }

    public String getFuncLabel() {
        return func.getName() + func.getBBList().indexOf(this);
    }

    public void merge(BasicBlockIR nextBB) {
        successors = nextBB.successors;
        tail.prev.next = nextBB.getHead().next;
        nextBB.getHead().next.prev = tail.prev;
        tail = nextBB.getTail();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        if (Config.DEBUG)
            return "_BB" + id + "_" + getFuncLabel() + '_' + lable;
        else
            return "_BB" + id;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
