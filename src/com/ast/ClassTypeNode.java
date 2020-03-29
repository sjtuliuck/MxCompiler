package com.ast;

import com.utility.Location;

public class ClassTypeNode extends TypeNode {
    public ClassTypeNode(Location location, String identifier) {
        super(location, identifier);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}