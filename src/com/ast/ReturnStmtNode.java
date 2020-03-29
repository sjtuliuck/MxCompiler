package com.ast;

import com.utility.Location;

public class ReturnStmtNode extends StmtNode {
    private ExprNode retExpr;

    public ReturnStmtNode(Location location, ExprNode retExpr) {
        super(location);
        this.retExpr = retExpr;
    }

    public ExprNode getRetExpr() {
        return retExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
