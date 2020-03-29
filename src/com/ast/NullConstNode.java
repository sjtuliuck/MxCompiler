package com.ast;

import com.utility.Location;

public class NullConstNode extends ConstExprNode {
    public NullConstNode(Location location) {
        super(location);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
