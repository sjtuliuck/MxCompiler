package com.frontend.entity;

import com.ast.ClassDefNode;
import com.ast.FuncDefNode;
import com.frontend.Scope;
import com.frontend.type.ClassType;
import com.frontend.type.Type;

public class ClassEntity extends Entity {
    private Scope classScope;
    private int memSize = 0;

    public ClassEntity(Type type, String identifier, Scope father) {
        super(identifier, type);
        classScope = new Scope(father);
    }

    public ClassEntity(ClassDefNode classDefNode, Scope father) {
        super(classDefNode.getIdentifier(), new ClassType(classDefNode.getIdentifier()));
        classScope = new Scope(father);
        for (FuncDefNode funcDefNode : classDefNode.getFuncDefNodeList()) {
            FuncEntity funcEntity = new FuncEntity(funcDefNode, classScope, classDefNode.getIdentifier());
            classScope.addFunc(funcEntity);
        }
    }

    public Scope getClassScope() {
        return classScope;
    }

    public void setClassScope(Scope classScope) {
        this.classScope = classScope;
    }

    public int getMemSize() {
        return memSize;
    }

    public void setMemSize(int memSize) {
        this.memSize = memSize;
    }
}
