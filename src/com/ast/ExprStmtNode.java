package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

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

    @Override
    public void dump(ASTPrinter printer, int tab) {
        Tools.printTab(tab);
        printer.visit(this);
    }

    @Override
    public String toString() {
        return "ExprStmtNode{" +
                "expr=" + expr +
                '}';
    }
}
