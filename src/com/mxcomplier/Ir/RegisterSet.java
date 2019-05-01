package com.mxcomplier.Ir;

import com.mxcomplier.Ir.Operands.PhysicalRegisterIR;

import java.security.PublicKey;

public class RegisterSet {

    static public PhysicalRegisterIR rax = new PhysicalRegisterIR("rax");
    static public PhysicalRegisterIR rcx = new PhysicalRegisterIR("rcx");
    static public PhysicalRegisterIR rdx = new PhysicalRegisterIR("rdx");
    static public PhysicalRegisterIR rbx = new PhysicalRegisterIR("rbx");
    static public PhysicalRegisterIR rsp = new PhysicalRegisterIR("rsp");
    static public PhysicalRegisterIR rbp = new PhysicalRegisterIR("rbp");
    static public PhysicalRegisterIR rsi = new PhysicalRegisterIR("rsi");
    static public PhysicalRegisterIR rdi = new PhysicalRegisterIR("rdi");
    static public PhysicalRegisterIR r8 = new PhysicalRegisterIR("r8");
    static public PhysicalRegisterIR r9 = new PhysicalRegisterIR("r9");
    static public PhysicalRegisterIR r10 = new PhysicalRegisterIR("r10");
    static public PhysicalRegisterIR r11 = new PhysicalRegisterIR("r11");
    static public PhysicalRegisterIR r12 = new PhysicalRegisterIR("r12");
    static public PhysicalRegisterIR r13 = new PhysicalRegisterIR("r13");
    static public PhysicalRegisterIR r14 = new PhysicalRegisterIR("r14");
    static public PhysicalRegisterIR r15 = new PhysicalRegisterIR("r15");

    public RegisterSet(){
        
    }
}
