package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

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

    @Override
    public void dump(ASTPrinter printer, int tab) {
        Tools.printTab(tab);
        printer.visit(this);
    }

    @Override
    public String toString() {
        return "ReturnStmtNode{" +
                "retExpr=" + retExpr +
                '}';
    }
}
