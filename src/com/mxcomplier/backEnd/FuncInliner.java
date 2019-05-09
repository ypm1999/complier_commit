package com.mxcomplier.backEnd;

import com.mxcomplier.FrontEnd.IRBuilder;
import com.mxcomplier.Ir.FuncIR;

import java.util.List;

public class FuncInliner extends IRScanner{

    void run(IRBuilder ir){
        List<FuncIR> funcList =  ir.root.getFuncs();
        for (FuncIR func : funcList){

        }

    }

}
