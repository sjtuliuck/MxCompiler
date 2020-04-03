package com.frontend.semantic;

import com.ast.*;
import com.frontend.Scope;
import com.frontend.entity.ClassEntity;
import com.frontend.entity.Entity;
import com.frontend.entity.FuncEntity;
import com.frontend.entity.VarEntity;
import com.frontend.type.ClassType;
import com.frontend.type.FuncType;
import com.frontend.type.IntType;
import com.frontend.type.Type;
import com.utility.CompileError;

import java.util.ArrayList;
import java.util.List;

public class GlobalScanner extends ScopeScanner {
    public GlobalScanner(Scope globalScope) {
        this.globalScope = globalScope;
    }

    private void checkMain() {
        FuncEntity entity = globalScope.getFunc("main");
//        System.out.println(entity.getRetType().getTypeName());
        if (entity == null) {
            throw new CompileError("no main function");
        } else if (entity.getParamList().size() != 0) {
            throw new CompileError("main with params");
        } else if (!(entity.getRetType() instanceof IntType)) {
            throw new CompileError("no return int");
        }
    }

    private void addBuildInFunc(Scope scope, Type retType, String identifier, List<VarEntity> params) {
        FuncEntity funcEntity = new FuncEntity(identifier, new FuncType(identifier));
        funcEntity.setRetType(retType);
        funcEntity.setParamList(params);
        funcEntity.setBuildInFunc(true);
        if (scope.getFather() != null) {
            funcEntity.setInClass(true);
        }
        scope.addFunc(funcEntity);
    }

    private void addAllBuildIn() {
    // array
        ClassEntity arrayEntity = new ClassEntity("#array#", new ClassType("#array#"), globalScope);
        globalScope.addClass(arrayEntity);
        Scope arrayScope = arrayEntity.getClassScope();
        // int size()
        addBuildInFunc(arrayScope, intType, "size", null);
    // string
        ClassEntity stringEntity = new ClassEntity("#string#", new ClassType("#string#"), globalScope);
        globalScope.addClass(stringEntity);
        Scope stringScope = stringEntity.getClassScope();
        // int length()
        List<VarEntity> params = new ArrayList<>();
        params.add(new VarEntity("this", stringEntity.getType()));
        addBuildInFunc(stringScope, intType, "length", params);
        // string substring(int left, int right)
        params = new ArrayList<>();
        params.add(new VarEntity("left", intType));
        params.add(new VarEntity("right", intType));
        params.add(new VarEntity("this", stringEntity.getType()));
        addBuildInFunc(stringScope, intType, "substring", params);
        // int parseInt()
        params = new ArrayList<>();
        params.add(new VarEntity("this", stringEntity.getType()));
        addBuildInFunc(stringScope, stringType, "parseInt", params);
        // int ord(int pos)
        params = new ArrayList<>();
        params.add(new VarEntity("pos", intType));
        params.add(new VarEntity("this", stringEntity.getType()));
        addBuildInFunc(stringScope, intType, "ord", params);
    // global
        // void print(string str)
        params = new ArrayList<>();
        params.add(new VarEntity("str", stringType));
        addBuildInFunc(globalScope, voidType, "print", params);
        // void println(string str)
        params = new ArrayList<>();
        params.add(new VarEntity("str", stringType));
        addBuildInFunc(globalScope, voidType, "println", params);
        // void printInt(int n)
        params = new ArrayList<>();
        params.add(new VarEntity("n", intType));
        addBuildInFunc(globalScope, voidType, "printInt", params);
        // void printlnInt(int n)
        params = new ArrayList<>();
        params.add(new VarEntity("n", intType));
        addBuildInFunc(globalScope, voidType, "printlnInt", params);
        // string getString()
        addBuildInFunc(globalScope, stringType, "getString", null);
        // int getInt()
        addBuildInFunc(globalScope, intType, "getInt", null);
        // string toString(int i)
        params = new ArrayList<>();
        params.add(new VarEntity("i", intType));
        addBuildInFunc(globalScope, stringType, "toString", params);
    }

    @Override
    public void visit(ProgramNode node) {
        addAllBuildIn();
        for (DefNode defNode : node.getDefNodeList()) {
            if (!(defNode instanceof VarDefNode)) {
                defNode.accept(this);
            }
        }
        checkMain();
    }

    @Override
    public void visit(FuncDefNode node) {
        Entity entity = new FuncEntity(node, globalScope);
        globalScope.addFunc(entity);
    }

    @Override
    public void visit(ClassDefNode node) {
        Entity entity = new ClassEntity(node, globalScope);
        globalScope.addClass(entity);
    }
}
