package com.mxcomplier.Type;

import com.mxcomplier.Config;

public class ClassType extends Type{
    private String name;

    public ClassType(String name) {
        this.name = name;
        this.hyperType = HyperType.CLASS;
        this.varSize = Config.getREGSIZE();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassType)
            return name.equals(((ClassType) obj).name);
        else
            return false;
    }

    @Override
    public String toString() {
        return String.format("ClassType(%s)", name.toString());
    }
}
