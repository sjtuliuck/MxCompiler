package com.ast;

import com.utility.Location;

public class IdentifierExprNode extends ExprNode {
    private String identifier;

    public IdentifierExprNode(Location location, String identifier) {
        super(location);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
