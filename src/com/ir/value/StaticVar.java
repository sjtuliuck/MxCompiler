package com.ir.value;

import com.ir.IRVisitor;

public class StaticVar extends StaticData {
    public StaticVar(String name, int size) {
        super(name, size);
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
