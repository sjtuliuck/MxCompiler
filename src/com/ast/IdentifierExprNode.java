package com.ast;

import com.frontend.ASTPrinter;
import com.frontend.entity.VarEntity;
import com.utility.Location;
import com.utility.Tools;

public class IdentifierExprNode extends ExprNode {
    private String identifier;
    private VarEntity varEntity;
    private boolean memAccessChecked, memAccessing;

    public IdentifierExprNode(Location location, String identifier) {
        super(location);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public VarEntity getVarEntity() {
        return varEntity;
    }

    public void setVarEntity(VarEntity varEntity) {
        this.varEntity = varEntity;
    }

    public boolean isMemAccessChecked() {
        return memAccessChecked;
    }

    public void setMemAccessChecked(boolean memAccessChecked) {
        this.memAccessChecked = memAccessChecked;
    }

    public boolean isMemAccessing() {
        return memAccessing;
    }

    public void setMemAccessing(boolean memAccessing) {
        this.memAccessing = memAccessing;
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
        return "IdentifierExprNode{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
