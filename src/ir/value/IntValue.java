package ir.value;

import ir.IRVisitor;

public abstract class IntValue {
    public abstract void accept(IRVisitor visitor);

    public abstract IntValue copy();
}
