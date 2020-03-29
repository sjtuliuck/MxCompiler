package com.ast;

import com.utility.Location;

public class IfStmtNode extends StmtNode {
    private ExprNode condExpr;
    private StmtNode thenStmt;
    private StmtNode elseStmt;

    public IfStmtNode(Location location, ExprNode condExpr, StmtNode thenStmt, StmtNode elseStmt) {
        super(location);
        this.condExpr = condExpr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    public ExprNode getCondExpr() {
        return condExpr;
    }

    public StmtNode getThenStmt() {
        return thenStmt;
    }

    public StmtNode getElseStmt() {
        return elseStmt;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
