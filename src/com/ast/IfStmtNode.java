package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

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

    @Override
    public void dump(ASTPrinter printer, int tab) {
        Tools.printTab(tab);
        printer.visit(this);
    }

    @Override
    public String toString() {
        return "IfStmtNode{" +
                "condExpr=" + condExpr +
                ", thenStmt=" + thenStmt +
                ", elseStmt=" + elseStmt +
                '}';
    }
}
