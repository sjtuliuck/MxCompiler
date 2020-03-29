package com.ast;

import com.utility.Location;

import java.util.List;

public class NewExprNode extends ExprNode {
    private String identifier;
    private List<ExprNode> exprNodeList;
    private int dim;

    public NewExprNode(Location location, String identifier, List<ExprNode> exprNodeList, int dim) {
        super(location);
        this.identifier = identifier;
        this.exprNodeList = exprNodeList;
        this.dim = dim;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<ExprNode> getExprNodeList() {
        return exprNodeList;
    }

    public int getDim() {
        return dim;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
