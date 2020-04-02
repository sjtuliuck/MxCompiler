package com.ast;

import com.frontend.type.Type;
import com.utility.*;

abstract public class ExprNode extends Node {
    protected Type type;
    protected boolean lvalue;

    public ExprNode(Location location) {
        super(location);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isLvalue() {
        return lvalue;
    }

    public void setLvalue(boolean lvalue) {
        this.lvalue = lvalue;
    }
}
