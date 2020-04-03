package com.ast;

import java.util.*;

import com.frontend.ASTPrinter;
import com.utility.*;

public class ProgramNode extends Node {
    List<DefNode> defNodeList;

    public ProgramNode(Location location, List<DefNode> defNodeList) {
        super(location);
        this.defNodeList = defNodeList;
    }

    public List<DefNode> getDefNodeList() {
        return defNodeList;
    }

    public void addDefNode(DefNode node) {
        defNodeList.add(node);
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
        return "<<AST ProgramNode>>\n" + defNodeList;
    }
}
