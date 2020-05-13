package com.frontend.type;

import com.utility.Tools;

public class Type {
    private String typeName;

    public Type(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getRegSize() {
        return Tools.regSize;
    }
}
