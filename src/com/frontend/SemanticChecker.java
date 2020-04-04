package com.frontend;

import com.ast.*;
import com.frontend.entity.ClassEntity;
import com.frontend.entity.Entity;
import com.frontend.entity.FuncEntity;
import com.frontend.entity.VarEntity;
import com.frontend.type.*;
import com.utility.CompileError;

import java.util.ArrayList;
import java.util.List;

public class SemanticChecker implements ASTVisitor {
    private int round;
    private Scope globalScope;
    //
    private Scope curScope;
    private FuncEntity curFuncEntity;
    private Type curRetType;
    //
    private ClassType classType;
    public Type boolType = new BoolType();
    public Type intType = new IntType();
    public Type stringType = new StringType();
    public Type nullType = new NullType();
    public Type voidType = new VoidType();

    public SemanticChecker(Scope globalScope) {
        round = 1;
        this.globalScope = globalScope;
        this.curScope = globalScope;
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
        ClassEntity arrayEntity = new ClassEntity(new ClassType("#array#"), "#array#", globalScope);
        globalScope.addClass(arrayEntity);
        Scope arrayScope = arrayEntity.getClassScope();
        // int size()
        addBuildInFunc(arrayScope, intType, "size", null);
        // string
        ClassEntity stringEntity = new ClassEntity(new ClassType("#string#"), "#string#", globalScope);
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
        addBuildInFunc(stringScope, stringType, "substring", params);
        // int parseInt()
        params = new ArrayList<>();
        params.add(new VarEntity("this", stringEntity.getType()));
        addBuildInFunc(stringScope, intType, "parseInt", params);
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

    private void checkVarInitExpr(VarNode varNode) {
        if (varNode.getInitExpr() == null) {
            return;
        }
        varNode.getInitExpr().accept(this);
        if (varNode.getType().getType() instanceof VoidType || varNode.getInitExpr().getType() instanceof VoidType) {
            throw new CompileError(varNode.getLocation(), "var init void error");
        }
        if (varNode.getType().getType() instanceof ArrayType && varNode.getInitExpr().getType() instanceof ArrayType) {
            Type lType = ((ArrayType) varNode.getType().getType()).getArrayType();
            Type rType = ((ArrayType) varNode.getInitExpr().getType()).getArrayType();
            while (lType instanceof ArrayType && rType instanceof ArrayType) {
                lType = ((ArrayType) lType).getArrayType();
                rType = ((ArrayType) rType).getArrayType();
            }
            if (!lType.getTypeName().equals(rType.getTypeName())) {
                throw new CompileError("var init array error");
            }
        }
        if ((varNode.getType().getType().getTypeName().equals(varNode.getInitExpr().getType().getTypeName()))) {
            return;
        }
        if (varNode.getInitExpr().getType() instanceof NullType) {
            if (!(varNode.getType().getType() instanceof ArrayType || varNode.getType().getType() instanceof ClassType))
                throw new CompileError(varNode.getLocation(), "var int null error");
        } else {
            throw new CompileError(varNode.getLocation(), "check var init not the same type");
        }
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
        round++;
        for (DefNode defNode : node.getDefNodeList()) {
            if (defNode instanceof ClassDefNode) {
                defNode.accept(this);
            }
        }
        round++;
        for (DefNode defNode : node.getDefNodeList()) {
            defNode.accept(this);
        }
    }

    @Override
    public void visit(VarDefNode node) {
        if (node.getVarNodeList() != null) {
            for (VarNode varNode : node.getVarNodeList()) {
                varNode.accept(this);
            }
        }
    }

    @Override
    public void visit(ClassDefNode node) {
        if (round == 1) {
            Entity entity = new ClassEntity(node, globalScope);
            globalScope.addClass(entity);
        } else if (round == 2) {
            ClassEntity classEntity = globalScope.getClass(node.getIdentifier());
            curScope = classEntity.getClassScope();
            classType = (ClassType) classEntity.getType();
            for (VarNode varNode : node.getVarNodeList()) {
                varNode.accept(this);
            }
            for (FuncDefNode funcDefNode : node.getFuncDefNodeList()) {
                funcDefNode.accept(this);
            }
            curScope = curScope.getFather();
            classType = null;
        } else {
            ClassEntity classEntity = (ClassEntity) curScope.getClass(node.getIdentifier());
            if (classEntity == null) {
                throw new CompileError(node.getLocation(), "class def 1 null entity");
            }
            curScope = classEntity.getClassScope();
            for (VarNode varNode : node.getVarNodeList()) {
                checkVarInitExpr(varNode);
            }
            for (FuncDefNode funcDefNode : node.getFuncDefNodeList()) {
                funcDefNode.accept(this);
            }
            curScope = curScope.getFather();
            if (curScope != globalScope) {
                throw new CompileError(node.getLocation(), "class def 2 class scope father is not global");
            }
        }
    }

    @Override
    public void visit(FuncDefNode node) {
        if (round == 1) {
            Entity entity = new FuncEntity(node, globalScope);
            globalScope.addFunc(entity);
        } else if (round == 2) {
            FuncEntity funcEntity = curScope.getFunc(node.getIdentifier());
            curScope = funcEntity.getFuncScope();
            VarEntity varEntity = new VarEntity("this", classType);
            curScope.addVar(varEntity);
            funcEntity.getParamList().add(varEntity);
            if ((node.getRetType().getType() instanceof NullType) && !(node.getIdentifier().equals(classType.getIdentifier()))) {
                throw new CompileError(node.getLocation(), "class scanner constructor error");
            }
            curScope = curScope.getFather();
        } else {
            FuncEntity funcEntity = (FuncEntity) curScope.getFunc(node.getIdentifier());
            if (funcEntity == null) {
                throw new CompileError(node.getLocation(), "func def 1 null entity");
            }
            if (funcEntity.getRetType() instanceof ClassType && curScope.getClass(((ClassType) (funcEntity.getRetType())).getIdentifier()) == null) {
                throw new CompileError(node.getLocation(), "func def 2 no class identifier in scope");
            }
            curScope = funcEntity.getFuncScope();
            curRetType = funcEntity.getRetType();
            if (node.getParamList() != null) {
                for (VarNode varNode : node.getParamList()) {
                    checkVarInitExpr(varNode);
                }
            }
            node.getStmt().accept(this);
            curScope = curScope.getFather();
        }
    }

    @Override
    public void visit(VarDefStmtNode node) {
        if (node.getVarNodeList() != null) {
            for (VarNode varNode : node.getVarNodeList()) {
                varNode.accept(this);
            }
        }
    }

    @Override
    public void visit(ExprStmtNode node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(IfStmtNode node) {
        // cond
        node.getCondExpr().accept(this);
        if (!(node.getCondExpr().getType() instanceof BoolType)) {
            throw new CompileError(node.getLocation(), "if cond not bool");
        }
        // then
        if (node.getThenStmt() != null) {
            if (!(node.getThenStmt() instanceof BlockStmtNode)) {
                Scope ifScope = new Scope(curScope);
                curScope = ifScope;
                node.getThenStmt().accept(this);
                curScope = curScope.getFather();
            } else {
                Scope blockScope = new Scope(curScope);
                ((BlockStmtNode) node.getThenStmt()).setScope(blockScope);
                curScope = blockScope;
                node.getThenStmt().accept(this);
                curScope = curScope.getFather();
            }
        }
        // else
        if (node.getElseStmt() != null) {
            if (!(node.getElseStmt() instanceof  BlockStmtNode)) {
                Scope ifScope = new Scope(curScope);
                curScope = ifScope;
                node.getElseStmt().accept(this);
                curScope = curScope.getFather();
            } else {
                Scope blockScope = new Scope(curScope);
                ((BlockStmtNode) node.getThenStmt()).setScope(blockScope);
                curScope = blockScope;
                node.getElseStmt().accept(this);
                curScope = curScope.getFather();
            }
        }
    }

    @Override
    public void visit(WhileStmtNode node) {
        // cond
        if (node.getCondExpr() != null) {
            node.getCondExpr().accept(this);
        } else {
            throw new CompileError(node.getLocation(), "while no cond");
        }
        // body
        if (node.getBodyStmt() != null) {
            if (!(node.getBodyStmt() instanceof BlockStmtNode)) {
                Scope loopScope = new Scope(curScope);
                loopScope.setInLoop(true);
                curScope = loopScope;
                node.getBodyStmt().accept(this);
                curScope = curScope.getFather();
            } else {
                Scope blockScope = new Scope(curScope);
                blockScope.setInLoop(true);
                ((BlockStmtNode) node.getBodyStmt()).setScope(blockScope);
                curScope = blockScope;
                node.getBodyStmt().accept(this);
                curScope = curScope.getFather();
            }
        }
    }

    @Override
    public void visit(ForStmtNode node) {
        // init
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        // cond
        if (node.getCondExpr() != null) {
            node.getCondExpr().accept(this);
            if (!(node.getCondExpr().getType() instanceof BoolType)) {
                throw new CompileError(node.getLocation(), "for cond not bool");
            }
        }
        // step
        if (node.getStepExpr() != null) {
            node.getStepExpr().accept(this);
        }
        // body
        if (node.getBodyStmt() != null) {
            if (!(node.getBodyStmt() instanceof BlockStmtNode)) {
                Scope loopScope = new Scope(curScope);
                loopScope.setInLoop(true);
                curScope = loopScope;
                node.getBodyStmt().accept(this);
                curScope = curScope.getFather();
            } else {
                Scope blockScope = new Scope(curScope);
                blockScope.setInLoop(true);
                ((BlockStmtNode) node.getBodyStmt()).setScope(blockScope);
                curScope = blockScope;
                node.getBodyStmt().accept(this);
                curScope = curScope.getFather();
            }
        }
    }

    @Override
    public void visit(ReturnStmtNode node) {
        if (node.getRetExpr() != null) {
            node.getRetExpr().accept(this);
            Type retType = node.getRetExpr().getType();
            if (retType instanceof VoidType || retType == null) {
                throw new CompileError(node.getLocation(), "return void error");
            }
            if (retType instanceof NullType) {
                if (!(curRetType instanceof ArrayType || curRetType instanceof ClassType)) {
                    throw new CompileError(node.getLocation(), "return null error");
                }
            } else if (!retType.getTypeName().equals(curRetType.getTypeName())) {
                throw new CompileError(node.getLocation(), "return not the same type");
            }
        } else if (!(curRetType instanceof VoidType || curRetType instanceof NullType || curRetType == null)) {
            throw new CompileError(node.getLocation(), "return void error");
        }
    }

    @Override
    public void visit(BreakStmtNode node) {
        if (!curScope.isInLoop()) {
            throw new CompileError(node.getLocation(), "break not in loop");
        }
//        currentScope = currentScope.getFather();
    }

    @Override
    public void visit(ContinueStmtNode node) {
        if (!curScope.isInLoop()) {
            throw new CompileError(node.getLocation(), "continue not in loop");
        }
    }

    @Override
    public void visit(BlockStmtNode node) {
        Scope blockScope = new Scope(curScope);
        curScope = blockScope;
        for (StmtNode stmtNode : node.getStmtNodeList()) {
            stmtNode.accept(this);
        }
        curScope = curScope.getFather();
    }

    @Override
    public void visit(ExprListNode node) {
        // empty
    }

    @Override
    public void visit(IdentifierExprNode node) {
        Entity entity = curScope.getVarFunc(node.getIdentifier());
        if (entity instanceof VarEntity) {
            node.setLvalue(true);
        } else if (entity instanceof FuncEntity) {
            node.setLvalue(false);
            curFuncEntity = (FuncEntity) entity;
        } else {
            throw new CompileError(node.getLocation(), "identifier expr error");
        }
        node.setType(entity.getType());
    }

    @Override
    public void visit(ThisExprNode node) {
        VarEntity varEntity = curScope.getVar("this");
        if (varEntity == null) {
            throw new CompileError(node.getLocation(), "this expr error");
        }
        node.setLvalue(false);
        node.setType(varEntity.getType());
    }

    @Override
    public void visit(MemberExprNode node) {
        node.getExpr().accept(this);
        String identifier;
        if (node.getExpr().getType() instanceof ArrayType) {
            identifier = "#array#";
        } else if (node.getExpr().getType() instanceof StringType) {
            identifier = "#string#";
        } else if (node.getExpr().getType() instanceof ClassType) {
            identifier = ((ClassType) node.getExpr().getType()).getIdentifier();
        } else {
            throw new CompileError(node.getLocation(), "member expr error");
        }
        ClassEntity classEntity = curScope.getClass(identifier);
        if (classEntity == null) {
            throw new CompileError(node.getLocation(), "member expr no class entity");
        }
        Entity entity = classEntity.getClassScope().getLocalVarFunc(node.getIdentifier());
        if (entity instanceof VarEntity) {
            node.setType(entity.getType());
        } else if (entity instanceof FuncEntity) {
            curFuncEntity = (FuncEntity) entity;
            node.setType(entity.getType());
        } else {
            throw new CompileError(node.getLocation(), "member not defined");
        }
        node.setLvalue(true);
    }

    @Override
    public void visit(ArrayExprNode node) {
        node.getArray().accept(this);
        if (!(node.getArray().getType() instanceof ArrayType)) {
            throw new CompileError(node.getLocation(), "array expr not array");
        }
        node.getIdx().accept(this);
        if (!(node.getIdx().getType() instanceof IntType)) {
            throw new CompileError(node.getLocation(), "array expr index not int");
        }
        node.setLvalue(true);
        node.setType(((ArrayType) node.getArray().getType()).getArrayType());
    }

    @Override
    public void visit(FuncExprNode node) {
        node.getExpr().accept(this);
        if (!(node.getExpr().getType() instanceof FuncType)) {
            throw new CompileError(node.getLocation(), "func expr not a function");
        }
        FuncEntity funcEntity = curFuncEntity;
        node.setFuncEntity(funcEntity);
        int paramNum;
        if (funcEntity.getParamList() != null && node.getParamList() != null) {
            if (funcEntity.isInClass()) {
                paramNum = funcEntity.getParamList().size() - 1;
            } else {
                paramNum = funcEntity.getParamList().size();
            }
            if (paramNum != node.getParamList().size()) {
                throw new CompileError(node.getLocation(), "func param number error");
            }
            for (int i = 0; i < paramNum; ++i) {
                Type paramType = funcEntity.getParamList().get(i).getType();
                node.getParamList().get(i).accept(this);
                if (node.getParamList().get(i).getType() instanceof VoidType) {
                    throw new CompileError(node.getLocation(), "func param void type");
                }
                if ((node.getParamList().get(i).getType() instanceof NullType)) {
                    if (!(paramType instanceof ArrayType || paramType instanceof ClassType)) {
                        throw new CompileError(node.getLocation(), "func param null type error");
                    }
                } else if (!node.getParamList().get(i).getType().getTypeName().equals(paramType.getTypeName())) {
                    throw new CompileError(node.getLocation(), "func param type not match");
                }

            }
        }
        node.setLvalue(false);
        node.setType(funcEntity.getRetType());
    }

    @Override
    public void visit(NewExprNode node) {
        if (node.getNewType() instanceof ClassType) {
            String classIdentifier = ((ClassType) node.getNewType()).getIdentifier();
            ClassEntity classEntity = curScope.getClass(classIdentifier);
            if (classEntity == null) {
                throw new CompileError(node.getLocation(), "new expr class not defined");
            }
        }
        if (node.getDim() != 0) {
            if (node.getExprNodeList() != null) {
                for (ExprNode exprNode : node.getExprNodeList()) {
                    exprNode.accept(this);
                    if (!(exprNode.getType() instanceof IntType)) {
                        throw new CompileError(node.getLocation(), "new index not int");
                    }
                }
            }
        }
        node.setLvalue(false);
        node.setType(node.getNewType());
    }

    @Override
    public void visit(PostfixExprNode node) {
        // postInc, postDec
        node.getExpr().accept(this);
        Type type = node.getExpr().getType();
        if (!(type instanceof IntType)) {
            throw new CompileError(node.getLocation(), "postfix not int ++--");
        }
        if (!node.getExpr().isLvalue()) {
            throw new CompileError(node.getLocation(), "postfix can't be lvalue");
        }
        node.setLvalue(false);
        node.setType(intType);
    }

    @Override
    public void visit(PrefixExprNode node) {
        // preInc, preDec, signPos, signNeg, bitwiseNot, logicNot
        node.getExpr().accept(this);
        Type type = node.getExpr().getType();
        PrefixExprNode.PreOp op = node.getOp();
        switch (op) {
            case preInc:
            case preDec:
                if (!(type instanceof IntType)) {
                    throw new CompileError(node.getLocation(), "prefix not int ++--");
                }
                if (!node.getExpr().isLvalue()) {
                    throw new CompileError(node.getLocation(), "prefix cant't be lvalue");
                }
                node.setLvalue(true);
                node.setType(intType);
                break;
            case signPos:
            case signNeg:
            case bitwiseNot:
                if (!(type instanceof IntType)) {
                    throw new CompileError(node.getLocation(), "prefix not int +-~");
                }
                node.setLvalue(false);
                node.setType(intType);
                break;
            case logicNot:
                if (!(type instanceof BoolType)) {
                    throw new CompileError(node.getLocation(), "prefix not bool !");
                }
                node.setLvalue(false);
                node.setType(boolType);
                break;
        }
    }

//    mul, div, mod,
//    add, sub,
//    shiftRight, shiftLeft,
//    less, greater, leq, geq,
//    neq, equal,
//    bitwiseAnd, bitwiseXor, bitwiseOr,
//    logicAnd, logicOr,
//    assign
    @Override
    public void visit(BinaryExprNode node) {
        node.getLhs().accept(this);
        Type ltype = node.getLhs().getType();
        node.getRhs().accept(this);
        Type rtype = node.getRhs().getType();
        if (ltype instanceof VoidType || rtype instanceof VoidType) {
            throw new CompileError(node.getLocation(), "binary expr void");
        }
        BinaryExprNode.BinOp op = node.getOp();
        switch (op) {
            case add:
                if (ltype instanceof StringType && rtype instanceof StringType) {
                    node.setLvalue(false);
                    node.setType(stringType);
                    break;
                }
            case sub:
            case mul:
            case div:
            case mod:
            case shiftRight:
            case shiftLeft:
            case bitwiseAnd:
            case bitwiseXor:
            case bitwiseOr:
                if (!(ltype instanceof IntType && rtype instanceof IntType)) {
                    throw new CompileError(node.getLocation(), "binary not int");
                }
                node.setLvalue(false);
                node.setType(intType);
                break;
            case less:
            case greater:
            case leq:
            case geq:
                if (!(ltype.getTypeName().equals(rtype.getTypeName()))) {
                    throw new CompileError(node.getLocation(), "binary not comparable");
                }
                if (!(ltype instanceof IntType || ltype instanceof StringType)) {
                    throw new CompileError(node.getLocation(), "binary not int/string");
                }
                node.setLvalue(false);
                node.setType(boolType);
                break;
            case equal:
            case neq:
                if (!(ltype.getTypeName().equals(rtype.getTypeName()))) {
                    if ((rtype instanceof NullType) && !(ltype instanceof ArrayType || ltype instanceof ClassType)) {
                        throw new CompileError(node.getLocation(), "binary null error");
                    }
                }
                node.setLvalue(false);
                node.setType(boolType);
                break;
            case logicAnd:
            case logicOr:
                if (!ltype.equals(rtype)) {
                    throw new CompileError(node.getLocation(), "binary not comparable");
                }
                if (!(ltype instanceof BoolType)) {
                    throw new CompileError(node.getLocation(), "logic op not bool");
                }
                node.setLvalue(false);
                node.setType(boolType);
                break;
            case assign:
                if (!(node.getLhs().isLvalue())) {
                    throw new CompileError(node.getLocation(), "assign can't be lvalue");
                }
                if (ltype instanceof ArrayType && rtype instanceof ArrayType) {
                    Type lType = ((ArrayType) ltype).getArrayType();
                    Type rType = ((ArrayType) rtype).getArrayType();
                    while (lType instanceof ArrayType && rType instanceof ArrayType) {
                        lType = ((ArrayType) lType).getArrayType();
                        rType = ((ArrayType) rType).getArrayType();
                    }
                    if (!lType.getTypeName().equals(rType.getTypeName())) {
                        throw new CompileError("assign array error");
                    }
                } else if (rtype instanceof NullType) {
                    if (!(ltype instanceof ArrayType || ltype instanceof ClassType))
                        throw new CompileError(node.getLocation(), "assign null error");
                } else if (!ltype.getTypeName().equals(rtype.getTypeName())) {
                    throw new CompileError(node.getLocation(), "assign type not the same");
                }
                node.setLvalue(false);
                node.setType(ltype);
                break;
            default:
                throw new CompileError(node.getLocation(), "binary op invalid");
        }
    }

    @Override
    public void visit(BoolConstNode node) {
        node.setLvalue(false);
        node.setType(boolType);
    }

    @Override
    public void visit(IntConstNode node) {
        node.setLvalue(false);
        node.setType(intType);
    }

    @Override
    public void visit(StringConstNode node) {
        node.setLvalue(false);
        node.setType(stringType);
    }

    @Override
    public void visit(NullConstNode node) {
        node.setLvalue(false);
        node.setType(nullType);
    }

    @Override
    public void visit(TypeNode node) {
        // empty
    }

    @Override
    public void visit(VarNode node) {
        if (round == 2) {
            if (node.getType().getType() instanceof ClassType) {
                if (curScope.getClass(((ClassType) (node.getType().getType())).getIdentifier()) == null) {
                    throw new CompileError(node.getLocation(), "class scanner varNode error");
                }
            }
            //
            Entity entity = new VarEntity(node, classType.getIdentifier());
            curScope.addVar(entity);
            return;
        }

        if ((node.getType().getType() instanceof ClassType) && (curScope.getClass(((ClassType) node.getType().getType()).getIdentifier())) == null) {
            throw new CompileError(node.getLocation(), "class identifier not defined");
        }
        checkVarInitExpr(node);
        VarEntity varEntity = new VarEntity(node);
        if (curScope.getFather() == null) {
            varEntity.setInGlobal(true);
        }
        curScope.addVar(varEntity);
    }

    @Override
    public void visit(VarListNode node) {
        // empty
    }
}
