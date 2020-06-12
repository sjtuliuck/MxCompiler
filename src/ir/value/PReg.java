package ir.value;

import ir.IRVisitor;

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
