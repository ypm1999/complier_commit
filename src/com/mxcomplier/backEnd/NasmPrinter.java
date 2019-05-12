package com.mxcomplier.backEnd;

import com.mxcomplier.Config;
import com.mxcomplier.Error.ComplierError;
import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.StaticDataIR;
import com.mxcomplier.Ir.ProgramIR;

import java.io.*;
import java.util.List;

public class NasmPrinter extends IRScanner {
    private String indentation = "";
    private PrintStream output;

    public NasmPrinter(IRBuilder builder, PrintStream output) {
        this.output = output;
        this.builder = builder;
        if (Config.DEBUG) {
            try {
                this.output = new PrintStream("test.asm");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void println(String str) {
        str = str.replace("\n", "\n" + indentation);
        output.println(indentation + str);
    }


    private void indent() {
        indentation += '\t';
    }

    private void unindent() {
        indentation = indentation.substring(1);
    }

    private void init_print(List<StaticDataIR> staticData) {
        try {
            BufferedReader libBuffer = new BufferedReader(new FileReader("lib/c2nasm/lib.asm"));
            String line;
            while ((line = libBuffer.readLine()) != null)
                output.println(line);

        } catch (IOException e) {
            throw new ComplierError("IO exception when reading builtin functions from file");
        }
        println("\n;********************************************************************************\n");
        println("global main");
        println("global __init");
        println("section .data");
        indent();
        for (StaticDataIR data : staticData) {
            println(data.lable + ':');
            indent();
            println(data.nasmString());
            unindent();
        }
        unindent();
        println("");
        println("section .text\n");
    }


    @Override
    public void visit(BasicBlockIR node) {
        InstIR inst = node.getHead().next;
        indent();
        while (inst != node.getTail()) {
            inst.accept(this);
            inst = inst.next;
        }
        unindent();
    }

    @Override
    public void visit(ProgramIR node) {
        init_print(node.getStaticData());
        for (FuncIR func : node.getFuncs()) {
            func.accept(this);
        }
        output.flush();
    }

    @Override
    public void visit(FuncIR node) {
        println(String.format("%s:", node.getName()));
        indent();
        for (BasicBlockIR bb : node.getBBList()) {
            println(String.format("%s:", bb));
            bb.accept(this);
        }
        unindent();
//        println("\n;********************************************************************************\n");
    }


    @Override
    public void visit(CallInstIR node) {
        println(node.nasmString());
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
        println(node.nasmString());
    }
//
//    @Override
//    public void visit(CompInstIR node) {
//        println(node.nasmString());
//    }

    @Override
    public void visit(BinaryInstIR node) {
        println(node.nasmString());
    }

    @Override
    public void visit(MoveInstIR node) {
        println(node.nasmString());
    }

    @Override
    public void visit(PopInstIR node) {
        println(node.nasmString());
    }

    @Override
    public void visit(PushInstIR node) {
        println(node.nasmString());
    }

    @Override
    public void visit(ReturnInstIR node) {
        println(node.toString());
    }

    @Override
    public void visit(LeaInstIR node) {
        println(node.nasmString());
    }
}
