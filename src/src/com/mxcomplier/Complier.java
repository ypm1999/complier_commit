package com.mxcomplier;

import com.mxcomplier.AST.ProgramNode;

import java.io.InputStream;

public class Complier {
    private ProgramNode ast;
    private InputStream codeInput;

    public Complier(InputStream codeInput){
        this.codeInput = codeInput;
    }

    private void buildAST() throws Exception  {

    }


    public boolean run() throws Exception {
        try{
            buildAST();
            return true;
        }
        catch (Error error){
            return false;
        }
    }
}
