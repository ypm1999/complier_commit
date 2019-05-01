package com.mxcomplier.backEnd;

import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.ProgramIR;

public class IRPrinter extends IRScanner {
    private String indentation = "";

    public IRPrinter(IRBuilder builder){
        this.builder = builder;
    }

    private void println(String str){
        str = str.replace("\n", "\n"+indentation);
        System.out.println(indentation+str);
    }


    private void indent(){
        indentation += '\t';
    }

    private void unindent(){
        indentation = indentation.substring(1);
    }


    @Override
    public void visit(BasicBlockIR node) {
        InstIR inst = node.getHead().next;
        indent();
        while(inst != node.getTail()){
            inst.accept(this);
            inst = inst.next;
        }
        unindent();
    }

    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs()){
            func.accept(this);
        }
    }

    @Override
    public void visit(FuncIR node) {
        println(String.format("<%s>", node.getName()));
        indent();
        for (BasicBlockIR bb : node.getBBList()){
            println(String.format("<%s>", bb));
            bb.accept(this);
        }
        unindent();
        println("\n********************************************************************************\n");
    }


    @Override
    public void visit(CallInstIR node) {
        println(node.toString());
    }

    @Override
    public void visit(UnaryInstIR node) {
        println(node.toString());
    }

    @Override
    public void visit(JumpInstIR node) {
        println(node.toString());
    }

    @Override
    public void visit(CJumpInstIR node) {
        println(node.toString());
    }

    @Override
    public void visit(BinaryInstIR node) {
        println(node.toString());
    }

    @Override
    public void visit(MoveInstIR node) {
        println(node.toString());
    }


    @Override
    public void visit(ReturnInstIR node) {
        println(node.toString());
    }

}
