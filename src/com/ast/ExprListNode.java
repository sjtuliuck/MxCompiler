package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

import java.util.List;

public class ExprListNode extends Node {
    private List<ExprNode> exprList;

    public ExprListNode(Location location, List<ExprNode> exprList) {
        super(location);
        this.exprList = exprList;
    }

    public List<ExprNode> getExprList() {
        return exprList;
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
        return "ExprListNode{" +
                "exprList=" + exprList +
                '}';
    }
}
