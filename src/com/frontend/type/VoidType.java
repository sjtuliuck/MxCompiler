package com.frontend.type;

public class VoidType extends Type {
    public VoidType() {
        super("void");
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(VoidType.class);
    }
}
