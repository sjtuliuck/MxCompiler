package com.ast;

import com.utility.Location;

public class VarNode extends DefNode {
    private TypeNode type;
    private String identifier;
    private ExprNode initExpr;

    public VarNode(Location location, TypeNode type, String identifier, ExprNode initExpr) {
        super(location);
        this.type = type;
        this.identifier = identifier;
        this.initExpr = initExpr;
    }

    public TypeNode getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ExprNode getInitExpr() {
        return initExpr;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setInitExpr(ExprNode initExpr) {
        this.initExpr = initExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
