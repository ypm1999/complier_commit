package com.mxcomplier;

public class Config {
    private static final int REGSIZE = 8;
    public static boolean DEBUG = false;
    public static boolean enableFuncInline = true;
    public static boolean enableBlockMerge = true;
    public static boolean enableBlockCopy = true;
    public static boolean enableFinalBlockMerge = true;
    public static boolean enableFinalBlockCopy = true;
    public static boolean enableEmptyForEliminate = true;
    public static boolean enableDeadCodeEliminate = true;
    public static boolean enableInstructionMatch = true;
    public static boolean enableLocalValueNumbering = true;





    public static int getREGSIZE() {
        return REGSIZE;
    }
}
