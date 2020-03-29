package com.ast;

import com.utility.Location;

public class BoolConstNode extends ConstExprNode {
    private boolean value;

    public BoolConstNode(Location location, boolean value) {
        super(location);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
