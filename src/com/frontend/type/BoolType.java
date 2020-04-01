package com.frontend.type;

public class BoolType extends Type {
    public BoolType() {
        super("bool");
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(BoolType.class);
    }

}
