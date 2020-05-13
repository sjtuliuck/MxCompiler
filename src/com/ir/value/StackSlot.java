package com.ir.value;

import com.ir.IRFunction;
import com.ir.IRVisitor;

public class StackSlot extends Register {
    private String identifier;
    private IRFunction parentFunction;

    public StackSlot(String identifier, IRFunction parentFunction) {
        this.identifier = identifier;
        this.parentFunction = parentFunction;
        parentFunction.getStackSlotList().add(this);
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntValue copy() {
        return null;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public IRFunction getParentFunction() {
        return parentFunction;
    }

    public void setParentFunction(IRFunction parentFunction) {
        this.parentFunction = parentFunction;
    }
}
