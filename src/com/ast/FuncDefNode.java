package com.ast;

import com.utility.Location;

import java.util.List;

public class FuncDefNode extends DefNode {
    private TypeNode retType;
    private String identifier;
    private List<VarNode> paramList;
    private StmtNode stmt;

    public FuncDefNode(Location location, TypeNode retType, String identifier, List<VarNode> paramList, StmtNode stmt) {
        super(location);
        this.retType = retType;
        this.identifier = identifier;
        this.paramList = paramList;
        this.stmt = stmt;
    }

    public TypeNode getRetType() {
        return retType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<VarNode> getParamList() {
        return paramList;
    }

    public StmtNode getStmt() {
        return stmt;
    }

    public void addParam(VarNode node) {
        paramList.add(node);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
