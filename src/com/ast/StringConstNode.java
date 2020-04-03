package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

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

    @Override
    public void dump(ASTPrinter printer, int tab) {
        Tools.printTab(tab);
        printer.visit(this);
    }

    @Override
    public String toString() {
        return "StringConstNode{" +
                "value='" + value + '\'' +
                '}';
    }
}
