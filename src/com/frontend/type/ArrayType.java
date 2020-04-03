package com.frontend.type;

import java.util.ArrayList;
import java.util.Objects;

public class ArrayType extends Type {
    private Type arrayType;

    public ArrayType(Type arrayType) {
        super("array");
        this.arrayType = arrayType;
    }

    public Type getArrayType() {
        return arrayType;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == ArrayType.class) && ((ArrayType) obj).getArrayType().equals(arrayType);
    }


    @Override
    public String toString() {
        return "ArrayType{" +
                "arrayType=" + arrayType +
                '}';
    }
}
