package com.mxcomplier.Error;

public class IRError extends Error {

    public IRError(String msg) {
        String errorMsg = String.format("[IR Error]: %s", msg);
        System.out.println(errorMsg);
    }

    public IRError() {
        System.out.println("[IR Error]: Unknown error");
    }
}
