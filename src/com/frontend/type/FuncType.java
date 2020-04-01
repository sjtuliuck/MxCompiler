package com.frontend.type;

public class FuncType extends Type {
    String identifier;

    public FuncType(String identifier) {
        super("function");
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == FuncType.class) && ((FuncType) obj).identifier.equals(identifier);
    }

    @Override
    public String toString() {
        return "FuncType{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
