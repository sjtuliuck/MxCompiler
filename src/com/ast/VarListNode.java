package com.ast;

import com.utility.Location;

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
}
