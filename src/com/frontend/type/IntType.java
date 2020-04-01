package com.frontend.type;

public class IntType extends Type {
    public IntType() {
        super("int");
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(IntType.class);
    }
}
