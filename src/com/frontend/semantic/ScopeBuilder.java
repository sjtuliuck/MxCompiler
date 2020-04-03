package com.frontend.semantic;

import com.ast.*;
import com.frontend.Scope;
import com.frontend.entity.ClassEntity;
import com.frontend.entity.Entity;
import com.frontend.entity.FuncEntity;
import com.frontend.entity.VarEntity;
import com.frontend.type.*;
import com.utility.CompileError;

public class ScopeBuilder extends ScopeScanner {
    private Scope currentScope;
    private FuncEntity currentFuncCallEntity;
    private Type currentRetType;

    public ScopeBuilder(Scope globalScope) {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
    }

    private void checkVarInitExpr(VarNode varNode) {
        if (varNode.getInitExpr() == null) {
            return;
        }
        varNode.getInitExpr().accept(this);
        if (varNode.getType().getType() instanceof VoidType || varNode.getInitExpr().getType() instanceof VoidType) {
            throw new CompileError(varNode.getLocation(), "check var init 1");
        }  else if (varNode.getInitExpr().getType() instanceof NullType && !(varNode.getType().getType() instanceof ArrayType || varNode.getType().getType() instanceof ClassType)) {
            throw new CompileError(varNode.getLocation(), "check var init 2");
        } else if (!(varNode.getType().getType().getTypeName().equals(varNode.getInitExpr().getType().getTypeName()))) {
            throw new CompileError(varNode.getLocation(), "check var init 3");
        }
    }

    @Override
    public void visit(ProgramNode node) {
        for (DefNode defNode : node.getDefNodeList()) {
            defNode.accept(this);
        }
    }

    // fixme
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
        ClassEntity classEntity = (ClassEntity) currentScope.getClass(node.getIdentifier());
        if (classEntity == null) {
            throw new CompileError(node.getLocation(), "class def 1 null entity");
        }
        currentScope = classEntity.getClassScope();
        for (VarNode varNode : node.getVarNodeList()) {
            checkVarInitExpr(varNode);
        }
        for (FuncDefNode funcDefNode : node.getFuncDefNodeList()) {
            funcDefNode.accept(this);
        }
        currentScope = currentScope.getFather();
        if (currentScope != globalScope) {
            throw new CompileError(node.getLocation(), "class def 2 class scope father is not global");
        }
    }

    @Override
    public void visit(FuncDefNode node) {
        FuncEntity funcEntity = (FuncEntity) currentScope.getFunc(node.getIdentifier());
        if (funcEntity == null) {
            throw new CompileError(node.getLocation(), "func def 1 null entity");
        }
        if (funcEntity.getRetType() instanceof ClassType && currentScope.getClass(((ClassType) (funcEntity.getRetType())).getIdentifier()) == null) {
            throw new CompileError(node.getLocation(), "func def 2 no class identifier in scope");
        }
        currentScope = funcEntity.getFuncScope();
        currentRetType = funcEntity.getRetType();
        if (node.getParamList() != null) {
            for (VarNode varNode : node.getParamList()) {
                checkVarInitExpr(varNode);
            }
        }
        node.getStmt().accept(this);
        currentScope = currentScope.getFather();
    }

    // fixme
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
        if (!(node.getThenStmt() instanceof BlockStmtNode)) {
            node.getThenStmt().accept(this);
        } else {
            Scope blockScope = new Scope(currentScope);
            ((BlockStmtNode) node.getThenStmt()).setScope(blockScope);
            currentScope = blockScope;
            node.getThenStmt().accept(this);
            currentScope = currentScope.getFather();
        }
        // else
        if (node.getElseStmt() != null) {
            if (!(node.getElseStmt() instanceof  BlockStmtNode)) {
                node.getElseStmt().accept(this);
            } else {
                Scope blockScope = new Scope(currentScope);
                ((BlockStmtNode) node.getThenStmt()).setScope(blockScope);
                currentScope = blockScope;
                node.getElseStmt().accept(this);
                currentScope = currentScope.getFather();
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
                node.getBodyStmt().accept(this);
            } else {
                Scope blockScope = new Scope(currentScope);
                blockScope.setInLoop(true);
                ((BlockStmtNode) node.getBodyStmt()).setScope(blockScope);
                currentScope = blockScope;
                node.getBodyStmt().accept(this);
                currentScope = currentScope.getFather();
            }
        } else {
            throw new CompileError(node.getLocation(), "while no body");
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
                node.getBodyStmt().accept(this);
            } else {
                Scope blockScope = new Scope(currentScope);
                blockScope.setInLoop(true);
                ((BlockStmtNode) node.getBodyStmt()).setScope(blockScope);
                currentScope = blockScope;
                node.getBodyStmt().accept(this);
                currentScope = currentScope.getFather();
            }
        } else {
            throw new CompileError(node.getLocation(), "for no body");
        }
    }

    // fixme
    @Override
    public void visit(ReturnStmtNode node) {
        if (node.getRetExpr() != null) {
            node.getRetExpr().accept(this);
            Type retType = node.getRetExpr().getType();
            if (retType instanceof VoidType || retType == null) {
                throw new CompileError(node.getLocation(), "return void error");
            }
            if (retType instanceof NullType && !(currentRetType instanceof ArrayType || currentRetType instanceof ClassType)) {
                throw new CompileError(node.getLocation(), "return null error");
            } else if (!retType.getTypeName().equals(currentRetType.getTypeName())) { // fixme
                throw new CompileError(node.getLocation(), "return not the same type");
            }
        } else if (!(currentRetType instanceof VoidType || currentRetType == null)) {
            throw new CompileError(node.getLocation(), "return void error");
        }
    }

    @Override
    public void visit(BreakStmtNode node) {
        if (!currentScope.isInLoop()) {
            throw new CompileError(node.getLocation(), "break not in loop");
        }
    }

    @Override
    public void visit(ContinueStmtNode node) {
        if (!currentScope.isInLoop()) {
            throw new CompileError(node.getLocation(), "continue not in loop");
        }
    }

    @Override
    public void visit(BlockStmtNode node) {
        Scope blockScope = new Scope(currentScope);
        if (currentScope.isInLoop()) {
            blockScope.setInLoop(true);
        }
        currentScope = blockScope;
        for (StmtNode stmtNode : node.getStmtNodeList()) {
            stmtNode.accept(this);
        }
        currentScope = currentScope.getFather();
    }

    @Override
    public void visit(ExprListNode node) {
        // empty
    }

    @Override
    public void visit(IdentifierExprNode node) {
        Entity entity = currentScope.getVarFunc(node.getIdentifier());
        if (entity instanceof VarEntity) {
            node.setLvalue(true);
        } else if (entity instanceof FuncEntity) {
            node.setLvalue(false);
            currentFuncCallEntity = (FuncEntity) entity;
        } else {
            throw new CompileError(node.getLocation(), "identifier expr error");
        }
        node.setType(entity.getType());
    }

    @Override
    public void visit(ThisExprNode node) {
        VarEntity varEntity = currentScope.getVar("this");
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
        ClassEntity classEntity = currentScope.getClass(identifier);
        if (classEntity == null) {
            throw new CompileError(node.getLocation(), "member expr no class entity");
        }
        Entity entity = classEntity.getClassScope().getLocalVarFunc(node.getIdentifier());
        if (entity instanceof VarEntity) {
            node.setType(entity.getType());
        } else if (entity instanceof FuncEntity) {
            currentFuncCallEntity = (FuncEntity) entity;
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
        FuncEntity funcEntity = currentFuncCallEntity;
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
                if ((node.getParamList().get(i).getType() instanceof NullType) && !(paramType instanceof ArrayType || paramType instanceof ClassType)) {
                    throw new CompileError(node.getLocation(), "func param null type error");
                }
                if (!node.getParamList().get(i).getType().getTypeName().equals(paramType.getTypeName())) {
                    throw new CompileError(node.getLocation(), "func param type not match");
                }
            }
        }
        node.setLvalue(false);
        node.setType(funcEntity.getRetType());
    }

    @Override
    public void visit(NewExprNode node) {
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
        node.setLvalue(true);
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
                if (!(ltype instanceof IntType || rtype instanceof IntType)) {
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
                if (!(ltype.equals(rtype))) {
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
                if ((node.getRhs().getType() instanceof NullType) && !(node.getLhs().getType() instanceof ArrayType) || node.getLhs().getType() instanceof ClassType) {
                    throw new CompileError(node.getLocation(), "assign null");
                }
                if (!node.getLhs().getType().equals(node.getRhs().getType())) {
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
        if ((node.getType().getType() instanceof ClassType) && (currentScope.getClass(((ClassType) node.getType().getType()).getIdentifier())) == null) {
            throw new CompileError(node.getLocation(), "class identifier not defined");
        }
        checkVarInitExpr(node);
        VarEntity varEntity = new VarEntity(node);
        if (currentScope.getFather() == null) {
            varEntity.setInGlobal(true);
        }
        currentScope.addVar(varEntity);
    }

    @Override
    public void visit(VarListNode node) {
        // empty
    }
}
