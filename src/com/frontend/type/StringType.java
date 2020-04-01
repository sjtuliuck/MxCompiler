package com.frontend.type;

public class StringType extends Type {
    public StringType() {
        super("string");
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(IntType.class);
    }
}
