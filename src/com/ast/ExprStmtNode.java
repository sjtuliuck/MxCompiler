package com.ast;

import com.utility.Location;

public class ExprStmtNode extends StmtNode {
    private ExprNode expr;

    public ExprStmtNode(Location location, ExprNode expr) {
        super(location);
        this.expr = expr;
    }

    public ExprNode getExpr() {
        return expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
