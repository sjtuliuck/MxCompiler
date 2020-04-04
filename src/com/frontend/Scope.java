package com.frontend;

import com.frontend.entity.ClassEntity;
import com.frontend.entity.Entity;
import com.frontend.entity.FuncEntity;
import com.frontend.entity.VarEntity;
import com.utility.CompileError;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private Map<String, Entity> entityMap = new HashMap<>();
    private Scope father;
    private boolean inLoop = false;
    private static final String varPrefix = "#var#";
    private static final String funcPrefix = "#func#";
    private static final String classPrefix = "#class#";


    public Scope(Scope father) {
        this.father = father;
    }

    public Map<String, Entity> getEntityMap() {
        return entityMap;
    }

    public Scope getFather() {
        return father;
    }

    public boolean isInLoop() {
        if (inLoop) {
            return true;
        } else if (father != null) {
            return father.isInLoop();
        } else {
            return false;
        }
    }

    public void setInLoop(boolean inLoop) {
        this.inLoop = inLoop;
    }

    public boolean hasIdentifier(String identifier) {
        return entityMap.containsKey(varPrefix + identifier) || entityMap.containsKey(funcPrefix + identifier) || entityMap.containsKey(classPrefix + identifier);
    }

    public void addVar(Entity entity) {
        if (!(entity instanceof VarEntity)) {
            throw new CompileError("Add Var Error 1");
        }
        String key = varPrefix + entity.getIdentifier();
        if (!hasIdentifier(entity.getIdentifier())) {
            entityMap.put(key, entity);
        } else {
            throw new CompileError("Add Var Error 2");
        }
    }

    public void addFunc(Entity entity) {
        if (!(entity instanceof FuncEntity)) {
            throw new CompileError("Add Func Error 1");
        }
        String key = funcPrefix + entity.getIdentifier();
        if (!hasIdentifier(entity.getIdentifier())) {
            entityMap.put(key, entity);
        } else {
            throw new CompileError("Add Func Error 2");
        }
    }

    public void addClass(Entity entity) {
        if (!(entity instanceof ClassEntity)) {
            throw new CompileError("Add Class Error 1");
        }
        String key = classPrefix + entity.getIdentifier();
        if (!hasIdentifier(entity.getIdentifier())) {
            entityMap.put(key, entity);
        } else {
            throw new CompileError("Add Class Error 2");
        }
    }

    public VarEntity getVar(String identifier) {
        String key = varPrefix + identifier;
        VarEntity varEntity = (VarEntity) entityMap.get(key);
        if (varEntity != null) {
            return varEntity;
        } else if (father != null) {
            return father.getVar(identifier);
        } else {
            return null;
        }
    }

    public FuncEntity getFunc(String identifier) {
        String key = funcPrefix + identifier;
        FuncEntity funcEntity = (FuncEntity) entityMap.get(key);
        if (funcEntity != null) {
            return funcEntity;
        } else if (father != null) {
            return father.getFunc(identifier);
        } else {
            return null;
        }
    }

    public Entity getVarFunc(String identifier) {
        Entity varEntity = entityMap.get(varPrefix + identifier);
        Entity funcEntity = entityMap.get(funcPrefix + identifier);
        if (varEntity != null) {
            return varEntity;
        } else if (funcEntity != null) {
            return funcEntity;
        } else if (father != null) {
            return father.getVarFunc(identifier);
        } else {
            return null;
        }
    }

    public ClassEntity getClass(String identifier) {
        String key = classPrefix + identifier;
        ClassEntity classEntity = (ClassEntity) entityMap.get(key);
        if (classEntity != null) {
            return classEntity;
        } else if (father != null) {
            return father.getClass(identifier);
        } else {
            return null;
        }
    }

    public VarEntity getLocalVar(String identifier) {
        VarEntity varEntity = (VarEntity) entityMap.get(varPrefix + identifier);
        return varEntity;
    }

    public Entity getLocalVarFunc(String identifier) {
        Entity varEntity = entityMap.get(varPrefix + identifier);
        Entity funcEntity = entityMap.get(funcPrefix + identifier);
        if (varEntity != null) {
            return varEntity;
        } else {
            return funcEntity;
        }
    }
}
