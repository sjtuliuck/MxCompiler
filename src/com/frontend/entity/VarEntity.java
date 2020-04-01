package com.frontend.entity;

import com.ast.VarNode;
import com.frontend.type.Type;
import com.utility.Location;

public class VarEntity extends Entity {
    private Location location;
    private boolean inGlobal = false;
    private boolean inClass = false;
    private String classIdentifier = null;

    //
    public VarEntity(String identifier, Type type) {
        super(identifier, type);
    }

    public VarEntity(VarNode varNode) {
        super(varNode.getIdentifier(), varNode.getType().getType());
        this.location = varNode.getLocation();
    }

    // in class
    public VarEntity(String identifier, Type type, String classIdentifier) {
        super(identifier, type);
        inClass = true;
        this.classIdentifier = classIdentifier;
    }

    public VarEntity(VarNode varNode, String classIdentifier) {
        super(varNode.getIdentifier(), varNode.getType().getType());
        inClass = true;
        this.classIdentifier = classIdentifier;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isInGlobal() {
        return inGlobal;
    }

    public boolean isInClass() {
        return inClass;
    }

    public String getClassIdentifier() {
        return classIdentifier;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setInGlobal(boolean inGlobal) {
        this.inGlobal = inGlobal;
    }

    public void setInClass(boolean inClass) {
        this.inClass = inClass;
    }

    public void setClassIdentifier(String classIdentifier) {
        this.classIdentifier = classIdentifier;
    }
}
