package com.ast;

import com.utility.Location;

public class IntConstNode extends ConstExprNode {
    private int value;

    public IntConstNode(Location location, int value) {
        super(location);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
