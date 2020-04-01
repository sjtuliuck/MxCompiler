package com.frontend.type;

public class NullType extends Type {
    public NullType() {
        super("null");
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(IntType.class);
    }
}
