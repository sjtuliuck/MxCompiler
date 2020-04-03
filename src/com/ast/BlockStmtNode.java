package com.ast;

import com.frontend.ASTPrinter;
import com.frontend.Scope;
import com.utility.Location;
import com.utility.Tools;

import java.util.List;

public class BlockStmtNode extends StmtNode {
    private List<StmtNode> stmtNodeList;
    private Scope scope;

    public BlockStmtNode(Location location, List<StmtNode> stmtNodeList) {
        super(location);
        this.stmtNodeList = stmtNodeList;
    }

    public List<StmtNode> getStmtNodeList() {
        return stmtNodeList;
    }

    public void setStmtNodeList(List<StmtNode> stmtNodeList) {
        this.stmtNodeList = stmtNodeList;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
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
        return "BlockStmtNode{" +
                "stmtNodeList=" + stmtNodeList +
                '}';
    }
}
