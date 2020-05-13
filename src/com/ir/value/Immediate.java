package com.ir.value;

import com.ir.IRVisitor;

public class Immediate extends IntValue {
    private int value;

    public Immediate(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntValue copy() {
        return new Immediate(value);
    }
}
