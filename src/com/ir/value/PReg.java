package com.ir.value;

import com.ir.IRVisitor;

public abstract class PReg extends Register {
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntValue copy() {
        return null;
    }
}
