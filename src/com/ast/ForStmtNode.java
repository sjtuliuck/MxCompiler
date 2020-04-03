package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

public class ForStmtNode extends StmtNode {
    private ExprNode initExpr;
    private ExprNode condExpr;
    private ExprNode stepExpr;
    private StmtNode bodyStmt;

    public ForStmtNode(Location location, ExprNode initExpr, ExprNode condExpr, ExprNode stepExpr, StmtNode bodyStmt) {
        super(location);
        this.initExpr = initExpr;
        this.condExpr = condExpr;
        this.stepExpr = stepExpr;
        this.bodyStmt = bodyStmt;
    }

    public ExprNode getInitExpr() {
        return initExpr;
    }

    public ExprNode getCondExpr() {
        return condExpr;
    }

    public ExprNode getStepExpr() {
        return stepExpr;
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
        return "ForStmtNode{" +
                "initExpr=" + initExpr +
                ", condExpr=" + condExpr +
                ", stepExpr=" + stepExpr +
                ", bodyStmt=" + bodyStmt +
                '}';
    }
}
