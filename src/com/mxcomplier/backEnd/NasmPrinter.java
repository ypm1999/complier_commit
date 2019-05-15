package com.mxcomplier.backEnd;

import com.mxcomplier.Config;
import com.mxcomplier.Error.ComplierError;
import com.mxcomplier.Error.IRError;
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

    private void setOrder(FuncIR func){
        func.initReverseOrderBBList();
        LinkedList<BasicBlockIR> BBList = new LinkedList<>();
        List<BasicBlockIR> oldBBList = new ArrayList<>(func.getBBList());
        List<BasicBlockIR> workList = new LinkedList<>();


        BBList.add(func.entryBB);
        workList.add(func.entryBB);
        for (BasicBlockIR bb: oldBBList){
            for (InstIR inst = bb.getHead().next; inst != bb.getTail(); inst = inst.next) {
                if (inst instanceof CJumpInstIR) {
                    CJumpInstIR cJumpInstIR = (CJumpInstIR) inst;
//                if (inst.getTrueBB().fronters.size() == 1)
//                    inst.reverseOp();
//                inst.append(new JumpInstIR(inst.getFalseBB()));
//                inst.removeFalseBB();
                    BasicBlockIR trueBB = cJumpInstIR.getTrueBB();
                    if (!BBList.contains(trueBB))
                        BBList.add(trueBB);
                    if (!workList.contains(trueBB) && trueBB != func.leaveBB)
                        workList.add(trueBB);

                }
            }
        }


        while(!workList.isEmpty()) {
            BasicBlockIR bb = workList.iterator().next();
            workList.remove(bb);
            if (!(bb.getTail().prev instanceof JumpInstIR))
                throw new IRError("bolck not end with Jump");
            BasicBlockIR nextBB = ((JumpInstIR) bb.getTail().prev).getTarget();
            if (BBList.contains(nextBB))
                continue;
            bb.getTail().prev.remove();
            if (nextBB.fronters.size() == 1){
                bb.merge(nextBB);
                if (nextBB != func.leaveBB && !workList.contains(bb))
                    workList.add(bb);
            }
            else{
                BBList.add(BBList.indexOf(bb) + 1, nextBB);
                if (nextBB != func.leaveBB && !workList.contains(nextBB))
                    workList.add(nextBB);
            }
        }
        func.setBBList(BBList);


    }
//    private void setOrder(FuncIR func){
//        func.initReverseOrderBBList();
//        LinkedList<BasicBlockIR> BBList = new LinkedList<>();
//        func.setBBList(BBList);
//        List<BasicBlockIR> orderedBBList = func.getReversedOrderedBBList();
//        Collections.reverse(orderedBBList);
//
//        for (BasicBlockIR bb: orderedBBList){
//            if (bb.getTail().prev instanceof CJumpInstIR) {
//                CJumpInstIR inst = (CJumpInstIR) bb.getTail().prev;
//                inst.append(new JumpInstIR(inst.getFalseBB()));
//                inst.removeFalseBB();
//            }
//        }
//
//        BBList.add(func.entryBB);
//        orderedBBList.remove(func.entryBB);
//        while (!orderedBBList.isEmpty()){
//            List<BasicBlockIR> removedBB = new LinkedList<>();
//
//            for (BasicBlockIR bb : orderedBBList){
//                BasicBlockIR prevBB = null;
//                for (BasicBlockIR prev : bb.fronters) {
//                    if (!(prev.getTail().prev instanceof JumpInstIR)
//                            || ((JumpInstIR)prev.getTail().prev).getTarget() != bb)
//                        continue;
//                    if(BBList.contains(prev)) {
//                        prevBB = prev;
//                        break;
//                    }
//                }
//
//                if (prevBB != null){
//                    prevBB.getTail().prev.remove();
//                    if (bb.fronters.size() == 1){
//                        prevBB.merge(bb);
//                    }
//                    else
//                        BBList.add(BBList.indexOf(prevBB) + 1, bb);
//                    removedBB.add(bb);
//                }
//            }
//            if (removedBB.isEmpty()) {
//                BBList.add(orderedBBList.iterator().next());
//                orderedBBList.remove(orderedBBList.iterator().next());
//            }
//            else
//                orderedBBList.removeAll(removedBB);
//        }
//    }


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
            if (func.getBBList().size() > 1)
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
