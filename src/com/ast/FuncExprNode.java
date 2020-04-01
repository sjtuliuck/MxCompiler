package com.ast;

import com.utility.Location;

import java.util.List;

public class FuncExprNode extends ExprNode {
    private ExprNode expr;
    private List<ExprNode> paramList;

    public FuncExprNode(Location location, ExprNode expr, List<ExprNode> paramList) {
        super(location);
        this.expr = expr;
        this.paramList = paramList;
    }

    public void setParamList(List<ExprNode> paramList) {
        this.paramList = paramList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "FuncExprNode{" +
                "expr=" + expr +
                ", paramList=" + paramList +
                '}';
    }
}
