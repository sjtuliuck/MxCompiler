package com.ast;

import com.utility.Location;

import java.util.List;

public class BlockStmtNode extends StmtNode {
    private List<StmtNode> stmtNodeList;

    public BlockStmtNode(Location location, List<StmtNode> stmtNodeList) {
        super(location);
        this.stmtNodeList = stmtNodeList;
    }

    public List<StmtNode> getStmtNodeList() {
        return stmtNodeList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
