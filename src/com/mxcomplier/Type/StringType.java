package com.mxcomplier.Type;

import com.mxcomplier.Config;

public class StringType extends Type {
    static private StringType instance = new StringType();

    private StringType() {
        this.hyperType = HyperType.STRING;
        this.varSize = Config.getREGSIZE();
    }

    static public StringType getInstance() {
        return instance;
    }
}
