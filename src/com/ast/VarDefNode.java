package com.ast;

import com.utility.*;

import java.util.List;

public class VarDefNode extends DefNode {
    private List<VarNode> varNodeList;

    public VarDefNode(Location location, List<VarNode> varNodeList) {
        super(location);
        this.varNodeList = varNodeList;
    }

    public List<VarNode> getVarNodeList() {
        return varNodeList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
