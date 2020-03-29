package com.ast;

import com.utility.Location;

abstract public class TypeNode extends Node {
    protected String identifier;

    public TypeNode(Location location, String identifier) {
        super(location);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
