package com.ast;

import com.frontend.ASTPrinter;
import com.frontend.type.Type;
import com.utility.Location;
import com.utility.Tools;

public class TypeNode extends Node {
    private Type type;

    public TypeNode(Location location, Type type) {
        super(location);
        this  .type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
}
