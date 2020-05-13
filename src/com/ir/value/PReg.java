package com.ir.value;

import com.ir.IRVisitor;

public abstract class PReg extends Register {
    public abstract boolean isGeneral();
    public abstract boolean isCallerSave();
    public abstract boolean isCalleeSave();
    public abstract String getName();

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntValue copy() {
        return null;
    }
}
