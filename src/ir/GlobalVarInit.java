package ir;

import ast.ExprNode;

public class GlobalVarInit {
    private String name;
    private ExprNode initValue;

    public GlobalVarInit(String name, ExprNode initValue) {
        this.name = name;
        this.initValue = initValue;
    }

    //
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExprNode getInitValue() {
        return initValue;
    }

    public void setInitValue(ExprNode initValue) {
        this.initValue = initValue;
    }
}
