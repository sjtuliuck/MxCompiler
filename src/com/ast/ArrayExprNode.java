package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

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

    @Override
    public void dump(ASTPrinter printer, int tab) {
        Tools.printTab(tab);
        printer.visit(this);
    }

    @Override
    public String toString() {
        return "ArrayExprNode{" +
                "array=" + array +
                ", idx=" + idx +
                '}';
    }
}
