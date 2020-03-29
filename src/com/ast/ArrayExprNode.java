package com.ast;

import com.utility.Location;

public class ArrayExprNode extends ExprNode {
    private ExprNode array;
    private ExprNode idx;

    public ArrayExprNode(Location location, ExprNode array, ExprNode idx) {
        super(location);
        this.array = array;
        this.idx = idx;
    }

    public ExprNode getArray() {
        return array;
    }

    public ExprNode getIdx() {
        return idx;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
