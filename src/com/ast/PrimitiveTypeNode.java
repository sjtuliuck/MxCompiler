package com.ast;

import com.utility.Location;

public class PrimitiveTypeNode extends TypeNode {
    public PrimitiveTypeNode(Location location, String identifier) {
        super(location, identifier);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
