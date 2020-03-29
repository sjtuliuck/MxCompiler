package com.ast;

import com.utility.Location;

public class StringConstNode extends ConstExprNode {
    private String value;

    public StringConstNode(Location location, String value) {
        super(location);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
