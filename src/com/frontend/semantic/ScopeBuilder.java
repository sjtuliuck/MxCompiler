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
        } else if (!varNode.getType().getType().equals(varNode.getInitExpr().getType())) {
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
        for (VarNode varNode : node.getVarNodeList()) {
            varNode.accept(this);
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

    @Override
    public void visit(VarDefStmtNode node) {
        // todo
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

    // fixme maybe wrong
    @Override
    public void visit(BlockStmtNode node) {
        for (StmtNode stmtNode : node.getStmtNodeList()) {
            stmtNode.accept(this);
        }
    }

    // todo
    @Override
    public void visit(ExprListNode node) {
        super.visit(node);
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
        // todo
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
        // todo
    }

    @Override
    public void visit(NewExprNode node) {
        // todo
    }

    @Override
    public void visit(PostfixExprNode node) {
        super.visit(node);
    }

    @Override
    public void visit(PrefixExprNode node) {
        super.visit(node);
    }

    @Override
    public void visit(BinaryExprNode node) {
        super.visit(node);
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
        super.visit(node);
    }

    @Override
    public void visit(VarNode node) {
        super.visit(node);
    }

    @Override
    public void visit(VarListNode node) {
        super.visit(node);
    }
}
