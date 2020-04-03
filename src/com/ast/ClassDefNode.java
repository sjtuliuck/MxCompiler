package com.ast;

import com.frontend.ASTPrinter;
import com.utility.Location;
import com.utility.Tools;

import java.util.*;


public class ClassDefNode extends DefNode {
    private String identifier;
    private List<VarNode> varNodeList;
    private List<FuncDefNode> funcDefNodeList;

    public ClassDefNode(Location location, String identifier, List<VarNode> varNodeList, List<FuncDefNode> funcDefNodeList) {
        super(location);
        this.identifier = identifier;
        this.varNodeList = varNodeList;
        this.funcDefNodeList = funcDefNodeList;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<VarNode> getVarNodeList() {
        return varNodeList;
    }

    public List<FuncDefNode> getFuncDefNodeList() {
        return funcDefNodeList;
    }

    public void addVar(VarNode node) {
        varNodeList.add(node);
    }


    public void addFunc(FuncDefNode node) {
        funcDefNodeList.add(node);
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
        return "ClassDefNode{" +
                "identifier='" + identifier + '\'' +
                ", varNodeList=" + varNodeList +
                ", funcDefNodeList=" + funcDefNodeList +
                '}';
    }
}
