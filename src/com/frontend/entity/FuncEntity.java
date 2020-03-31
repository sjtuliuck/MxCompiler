package com.frontend.entity;

import com.ast.StmtNode;
import com.ast.TypeNode;
import com.utility.Location;

import java.util.List;

public class FuncEntity extends Entity {
    public enum FuncEntityType {
        function, method, constructor
    }

    private TypeNode retType;
    private List<VarEntity> params;
    private StmtNode bodyStmt;
    private FuncEntityType entityType;

    public FuncEntity(Location location, String name, TypeNode retType, List<VarEntity> params, StmtNode bodyStmt, FuncEntityType entityType) {
        super(location, name);
        this.retType = retType;
        this.params = params;
        this.bodyStmt = bodyStmt;
        this.entityType = entityType;
    }
}
