package com.mxcomplier.Error;

import com.mxcomplier.AST.Location;

public class ComplierError extends Error {

    public ComplierError(Location location, String msg) {
        String errorMsg = String.format("[Complier Error] at (%d,%d): %s",
                location.getLine(), location.getColumn(), msg);
        System.out.println(errorMsg);
    }

    public ComplierError(String msg) {
        String errorMsg = String.format("[Complier Error]: %s", msg);
        System.out.println(errorMsg);
    }

    public ComplierError() {
        System.out.println("[Complier Error]: Unknown error");
    }
}
