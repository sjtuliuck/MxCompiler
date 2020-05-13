package com.ir.value;

import com.ir.IRVisitor;

public abstract class IntValue {
    public abstract void accept(IRVisitor visitor);

    public abstract IntValue copy();
}
