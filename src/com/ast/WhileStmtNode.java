package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

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

    @Override
    public void dump(ASTPrinter printer, int tab) {
        Tools.printTab(tab);
        printer.visit(this);
    }

    @Override
    public String toString() {
        return "WhileStmtNode{" +
                "condExpr=" + condExpr +
                ", bodyStmt=" + bodyStmt +
                '}';
    }
}
