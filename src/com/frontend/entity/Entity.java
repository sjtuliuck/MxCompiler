package com.frontend.entity;

import com.frontend.type.Type;

abstract public class Entity {
    private String identifier;
    private Type type;

    public Entity(String identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Type getType() {
        return type;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
