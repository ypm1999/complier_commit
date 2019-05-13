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
import java.util.*;

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

    class Counter{
        int value;
        Counter(int val){
            value = val;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    private void setOrder(FuncIR func){
        func.initReverseOrderBBList();
        LinkedList<BasicBlockIR> BBList = new LinkedList<>();
        func.setBBList(BBList);
        List<BasicBlockIR> orderedBBList = func.getReversedOrderedBBList();
        Collections.reverse(orderedBBList);

//        HashMap<BasicBlockIR, Counter> pathCounter = new HashMap<>();
//        for (BasicBlockIR bb : orderedBBList)
//            pathCounter.put(bb, new Counter(0));
//        pathCounter.get(func.entryBB).value++;
        for (BasicBlockIR bb: orderedBBList){
            if (bb.getTail().prev instanceof CJumpInstIR) {
                CJumpInstIR inst = (CJumpInstIR) bb.getTail().prev;
                inst.append(new JumpInstIR(inst.getFalseBB()));
                inst.removeFalseBB();
            }
        }

        BBList.add(func.entryBB);
        orderedBBList.remove(func.entryBB);
        while (!orderedBBList.isEmpty()){
            List<BasicBlockIR> removedBB = new LinkedList<>();

            for (BasicBlockIR bb : orderedBBList){
                BasicBlockIR prevBB = null;
                for (BasicBlockIR prev : bb.fronters) {
                    if (!(prev.getTail().prev instanceof JumpInstIR)
                            || ((JumpInstIR)prev.getTail().prev).getTarget() != bb)
                        continue;
                    if(BBList.contains(prev)) {
                        prevBB = prev;
                        break;
                    }
                }

                if (prevBB != null){
                    prevBB.getTail().prev.remove();
//                    if (bb.fronters.size() == 1){
//                        prevBB.merge(bb);
//                    }
                    BBList.add(BBList.indexOf(prevBB) + 1, bb);
                    removedBB.add(bb);
                }
            }
            if (removedBB.isEmpty()) {
                BBList.add(orderedBBList.iterator().next());
                orderedBBList.remove(orderedBBList.iterator().next());
            }
            else
                orderedBBList.removeAll(removedBB);
        }
//        for (BasicBlockIR bb : orderedBBList){
//
//            int count = pathCounter.get(bb).value;
//            for (BasicBlockIR nextBB : bb.successors)
//                pathCounter.get(nextBB).value += count;
//            if (bb == func.entryBB)
//                continue;
//
//            BasicBlockIR prevBB = null;
//            count = -1;
//            for (BasicBlockIR prev : bb.fronters) {
//                int tmp = pathCounter.get(prev).value;
//                if (!(prev.getTail().prev instanceof JumpInstIR)
//                        || ((JumpInstIR)prev.getTail().prev).getTarget() != bb)
//                    continue;
//                if (tmp > count){
//                    count = tmp;
//                    prevBB = prev;
//                }
//            }
//
//            if (prevBB == null || !BBList.contains(prevBB)){
//                BBList.addLast(bb);
//            }
//            else{
//                prevBB.getTail().prev.remove();
//                BBList.add(BBList.indexOf(prevBB) + 1, bb);
//            }
//        }
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
            setOrder(func);
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
