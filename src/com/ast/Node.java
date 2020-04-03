package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;

abstract public class Node {
    private Location location;

    public Node(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    abstract public void accept(ASTVisitor visitor);

    abstract public void dump(ASTPrinter printer, int tab);
}
