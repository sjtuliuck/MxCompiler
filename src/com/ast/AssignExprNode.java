package com.ast;

import com.utility.Location;

public class AssignExprNode extends ExprNode {
    private ExprNode lhs;
    private ExprNode rhs;

    public AssignExprNode(Location location, ExprNode lhs, ExprNode rhs) {
        super(location);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
