package com.ast;

import com.utility.Location;

public class WhileStmtNode extends StmtNode {
    private ExprNode condExpr;
    private StmtNode bodyStmt;

    public WhileStmtNode(Location location, ExprNode condExpr, StmtNode bodyStmt) {
        super(location);
        this.condExpr = condExpr;
        this.bodyStmt = bodyStmt;
    }

    public ExprNode getCondExpr() {
        return condExpr;
    }

    public StmtNode getBodyStmt() {
        return bodyStmt;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
