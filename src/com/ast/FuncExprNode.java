package com.ast;

import com.utility.Location;

import java.util.List;

public class FuncExprNode extends ExprNode {
    private String identifier;
    private List<ExprNode> paramList;

    public FuncExprNode(Location location, String identifier, List<ExprNode> paramList) {
        super(location);
        this.identifier = identifier;
        this.paramList = paramList;
    }

    public void setParamList(List<ExprNode> paramList) {
        this.paramList = paramList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
