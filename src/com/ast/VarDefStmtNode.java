package com.ast;

import com.utility.Location;

import java.util.List;

public class VarDefStmtNode extends StmtNode {
    private List<VarNode> varNodeList;

    public VarDefStmtNode(Location location, List<VarNode> varNodeList) {
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
