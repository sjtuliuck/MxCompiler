package com.frontend.semantic;

import com.ast.*;
import com.frontend.Scope;
import com.frontend.entity.ClassEntity;
import com.frontend.entity.Entity;
import com.frontend.entity.FuncEntity;
import com.frontend.entity.VarEntity;
import com.frontend.type.ClassType;
import com.frontend.type.NullType;
import com.utility.CompileError;

public class ClassScanner extends ScopeScanner {
    private Scope currentScope;
    private ClassType classType;

    public ClassScanner(Scope globalScope) {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
    }

    @Override
    public void visit(ProgramNode node) {
        for (DefNode defNode : node.getDefNodeList()) {
            if (defNode instanceof ClassDefNode) {
                defNode.accept(this);
            }
        }
    }

    @Override
    public void visit(ClassDefNode node) {
        ClassEntity classEntity = globalScope.getClass(node.getIdentifier());
        currentScope = classEntity.getClassScope();
        classType = (ClassType) classEntity.getType();
        for (VarNode varNode : node.getVarNodeList()) {
            varNode.accept(this);
        }
        for (FuncDefNode funcDefNode : node.getFuncDefNodeList()) {
            funcDefNode.accept(this);
        }
        currentScope = currentScope.getFather();
        classType = null;
    }

    @Override
    public void visit(VarNode node) {
        if (node.getType().getType() instanceof ClassType) {
            if (currentScope.getClass(((ClassType) (node.getType().getType())).getIdentifier()) == null) {
                throw new CompileError(node.getLocation(), "class scanner varNode error");
            }
        }
        //
        Entity entity = new VarEntity(node, classType.getIdentifier());
        currentScope.addVar(entity);
    }

    @Override
    public void visit(FuncDefNode node) {
        FuncEntity funcEntity = currentScope.getFunc(node.getIdentifier());
        currentScope = funcEntity.getFuncScope();
        VarEntity varEntity = new VarEntity("this", classType);
        currentScope.addVar(varEntity);
        funcEntity.getParamList().add(varEntity);
        if ((node.getRetType().getType() instanceof NullType) && !(node.getIdentifier().equals(classType.getIdentifier()))) {
            throw new CompileError(node.getLocation(), "class scanner constructor error");
        }
        currentScope = currentScope.getFather();
    }
}
