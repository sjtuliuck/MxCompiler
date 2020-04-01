package com.frontend.scope;

import com.frontend.entity.Entity;
import com.frontend.entity.FuncEntity;
import com.frontend.entity.VarEntity;
import com.utility.CompileError;

import java.util.*;

public class Scope {
    private Scope father;
    private Map<String, Entity> entities = new HashMap<>();

    private String varPrefix = "#var#";
    private String funcPrefix = "#func#";
    private String classPrefix = "#class#";

    public Scope() {
        this.father = null;
    }

    public Scope(Scope father) {
        this.father = father;
    }

    public Scope getFather() {
        return father;
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public boolean noIdentifier(String identifier) {
        return !(entities.containsKey(varPrefix + identifier) || entities.containsKey(funcPrefix + identifier) || entities.containsKey(classPrefix + identifier));
    }

    public VarEntity getVar(String identifier) {
        String key = varPrefix + identifier;
        VarEntity varEntity = (VarEntity) entities.get(key);
        if (varEntity != null) {
            return varEntity;
        } else if (father != null) {
            return father.getVar(identifier);
        } else {
            return null;
        }
    }

    public FuncEntity getFunc(String identifier) {
        String key = varPrefix + identifier;
        FuncEntity funcEntity = (FuncEntity) entities.get(key);
        if (funcEntity != null) {
            return funcEntity;
        } else if (father != null) {
            return father.getFunc(identifier);
        } else {
            return null;
        }
    }

    public ClassEntity getClass(String identifier) {
        String key = varPrefix + identifier;
        ClassEntity classEntity = (ClassEntity) entities.get(key);
        if (classEntity != null) {
            return classEntity;
        } else if (father != null) {
            return father.getClass(identifier);
        } else {
            return null;
        }
    }

    public void addVar(Entity entity) {
        if (!(entity instanceof VarEntity)) {
            throw new CompileError("add var error");
        }
        String key = varPrefix + entity.getIdentifier();
        if (noIdentifier(entity.getIdentifier())) {
            entities.put(key, entity);
        } else {
            throw new CompileError("add var error 2");
        }
    }

    public void addFunc(Entity entity) {
        if (!(entity instanceof FuncEntity)) {
            throw new CompileError("add func error");
        }
        String key = funcPrefix + entity.getIdentifier();
        if (noIdentifier(entity.getIdentifier())) {
            entities.put(key, entity);
        } else {
            throw new CompileError("add func error 2");
        }
    }

    public void addClass(Entity entity) {
        if (!(entity instanceof ClassEntity)) {
            throw new CompileError("add class error");
        }
        String key = classPrefix + entity.getIdentifier();
        if (noIdentifier(entity.getIdentifier())) {
            entities.put(key, entity);
        } else {
            throw new CompileError("add class error 2");
        }
    }
}
