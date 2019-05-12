package com.mxcomplier.backEnd;

import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.FuncIR;

public class UseLessCodeEliminater extends IRScanner {

    IRBuilder ir;
    public  UseLessCodeEliminater(IRBuilder ir){
        this.ir = ir;
    }

    public void run(){
        for (FuncIR funcIR: ir.root.getFuncs())
            runFunc(funcIR);
    }

    private void runFunc(FuncIR func){

    }

}
