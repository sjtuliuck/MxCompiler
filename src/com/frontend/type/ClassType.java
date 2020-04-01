package com.frontend.type;

public class ClassType extends Type {
    String identifier;

    public ClassType(String identifier) {
        super("class");
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj.getClass() == ClassType.class) && ((ClassType) obj).identifier.equals(identifier));
    }

    @Override
    public String toString() {
        return "ClassType{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
