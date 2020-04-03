package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

import java.util.*;

public class VarListNode extends DefNode {
    private List<VarNode> varNodeList;

    public VarListNode(Location location, List<VarNode> varNodeList) {
        super(location);
        this.varNodeList = varNodeList;
    }

    public List<VarNode> getVarNodeList() {
        return varNodeList;
    }

    public void addVarNode(VarNode node) {
        varNodeList.add(node);
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
        return "VarListNode{" +
                "varNodeList=" + varNodeList +
                '}';
    }
}
