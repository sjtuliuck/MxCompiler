package com.frontend.entity;

import com.ast.ExprNode;
import com.ast.TypeNode;
import com.utility.Location;

public class VarEntity extends Entity {
    public enum VarEntityType {
        global, local
    }

    private TypeNode type;
    private ExprNode initExpr;
    private VarEntityType entityType;

    public VarEntity(Location location, String name, TypeNode type, ExprNode initExpr, VarEntityType entityType) {
        super(location, name);
        this.type = type;
        this.initExpr = initExpr;
        this.entityType = entityType;
    }

    public TypeNode getType() {
        return type;
    }

    public ExprNode getInitExpr() {
        return initExpr;
    }

    public VarEntityType getEntityType() {
        return entityType;
    }
}
