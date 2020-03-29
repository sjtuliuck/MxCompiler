package com.ast;

import com.utility.Location;

public class ArrayTypeNode extends TypeNode {
    private TypeNode type;
    private int dim;

    public ArrayTypeNode(Location location, TypeNode type, int dim) {
        super(location, type.getIdentifier());
        this.dim = dim;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
