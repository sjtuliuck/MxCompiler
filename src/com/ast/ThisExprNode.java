package com.ast;

import com.utility.Location;

public class ThisExprNode extends ExprNode {
    public ThisExprNode(Location location) {
        super(location);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
