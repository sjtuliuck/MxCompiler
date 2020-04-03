package com.frontend;

import com.ast.*;
import com.frontend.type.ClassType;
import com.utility.Tools;

import javax.tools.Tool;
import java.io.PrintStream;

public class ASTPrinter implements ASTVisitor {
    int tab;
    public ASTPrinter() {
        tab = 0;
    }

    private void puts(String s) {
        System.out.println(s);
    }

    @Override
    public void visit(ProgramNode node) {
        puts("<ProgramNode>");
        ++tab;
        for (DefNode defNode : node.getDefNodeList()) {
            defNode.dump(this, tab);
        }
        --tab;
    }

    @Override
    public void visit(VarDefNode node) {
        puts("<VarDefNode>");
        ++tab;
        for (VarNode varNode : node.getVarNodeList()) {
            varNode.dump(this, tab);
        }
        --tab;
    }

    @Override
    public void visit(ClassDefNode node) {
        puts("<ClassDefNode>");
        ++tab;
        Tools.printTab(tab);
        puts("name:" + node.getIdentifier());
        for (VarNode varNode : node.getVarNodeList()) {
            varNode.dump(this, tab);
        }
        for (FuncDefNode funcDefNode : node.getFuncDefNodeList()) {
            funcDefNode.dump(this, tab);
        }
        --tab;
    }

    @Override
    public void visit(FuncDefNode node) {
        puts("<FuncDefNode>");
        ++tab;
        Tools.printTab(tab);
        puts("name:" + node.getIdentifier());
        node.getRetType().dump(this, tab);
        if (node.getParamList() != null) {
            for (VarNode varNode : node.getParamList()) {
                varNode.dump(this, tab);
            }
        }
        node.getStmt().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(VarDefStmtNode node) {
        puts("<VarDefStmtNode>");
        ++tab;
        for (VarNode varNode : node.getVarNodeList()) {
            varNode.dump(this, tab);
        }
        --tab;
    }

    @Override
    public void visit(ExprStmtNode node) {
        puts("<ExprStmtNode>");
        ++tab;
        node.getExpr().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(IfStmtNode node) {
        puts("<IfStmtNode>");
        ++tab;
        node.getCondExpr().dump(this, tab);
        node.getThenStmt().dump(this, tab);
        if (node.getElseStmt() != null)
            node.getElseStmt().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(WhileStmtNode node) {
        puts("<WhileStmtNode>");
        ++tab;
        node.getCondExpr().dump(this, tab);
        node.getBodyStmt().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(ForStmtNode node) {
        puts("<ForStmtNode>");
        ++tab;
        if (node.getInitExpr() != null)
            node.getInitExpr().dump(this, tab);
        if (node.getCondExpr() != null)
            node.getCondExpr().dump(this, tab);
        if (node.getStepExpr() != null)
            node.getStepExpr().dump(this, tab);
        node.getBodyStmt().dump(this ,tab);
        --tab;
    }

    @Override
    public void visit(ReturnStmtNode node) {
        puts("<ReturnStmtNode>");
        ++tab;
        if (node.getRetExpr() != null)
            node.getRetExpr().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(BreakStmtNode node) {
        puts("<BreakStmtNode>");
    }

    @Override
    public void visit(ContinueStmtNode node) {
        puts("<ContinueStmtNode>");
    }

    @Override
    public void visit(BlockStmtNode node) {
        puts("<BlockStmtNode>");
        ++tab;
        for (StmtNode stmtNode : node.getStmtNodeList()) {
            stmtNode.dump(this, tab);
        }
        --tab;
    }

    @Override
    public void visit(ExprListNode node) {
        puts("<ExprListNode>");
        ++tab;
        for (ExprNode exprNode : node.getExprList()) {
            exprNode.dump(this, tab);
        }
        --tab;
    }

    @Override
    public void visit(IdentifierExprNode node) {
        puts("<IdentifierExprNode>");
        ++tab;
        Tools.printTab(tab);
        puts("Identifier: " + node.getIdentifier());
        --tab;
    }

    @Override
    public void visit(ThisExprNode node) {
        puts("<ThisExprNode>");
    }

    @Override
    public void visit(MemberExprNode node) {
        puts("<MemberExprNode>");
        ++tab;
        node.getExpr().dump(this, tab);
        Tools.printTab(tab);
        puts("Identifier:" + node.getIdentifier());
        --tab;
    }

    @Override
    public void visit(ArrayExprNode node) {
        puts("<ArrayExprNode>");
        ++tab;
        node.getArray().dump(this, tab);
        node.getIdx().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(FuncExprNode node) {
        puts("<FuncExprNode>");
        ++tab;
        node.getExpr().dump(this, tab);
        if (node.getParamList() != null) {
            for (ExprNode exprNode : node.getParamList()) {
                exprNode.dump(this, tab);
            }
        }
        --tab;
    }

    @Override
    public void visit(NewExprNode node) {
        puts("<NewExprNode>");
        ++tab;
        Tools.printTab(tab);
        puts("TypeName:" + node.getNewType().getTypeName());
        Tools.printTab(tab);
        if (node.getNewType() instanceof ClassType) {
            puts("Class: " + ((ClassType) node.getNewType()).getIdentifier());
        }
        if (node.getExprNodeList() != null) {
            for (ExprNode exprNode : node.getExprNodeList()) {
                exprNode.dump(this, tab);
            }
        }
        Tools.printTab(tab);
        System.out.println("Dim: " + node.getDim());
        --tab;
    }

    @Override
    public void visit(PostfixExprNode node) {
        puts("<PostfixExprNode>");
        ++tab;
        Tools.printTab(tab);
        puts("op:" + node.getOp().toString());
        node.getExpr().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(PrefixExprNode node) {
        puts("<PrefixExprNode>");
        ++tab;
        Tools.printTab(tab);
        puts("op:" + node.getOp().toString());
        node.getExpr().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(BinaryExprNode node) {
        puts("<BinaryExprNode>");
        ++tab;
        Tools.printTab(tab);
        puts("op:" + node.getOp().toString());
        node.getLhs().dump(this, tab);
        node.getRhs().dump(this, tab);
        --tab;
    }

    @Override
    public void visit(BoolConstNode node) {
        puts("<BoolConstNode>");
        ++tab;
        Tools.printTab(tab);
        puts("Bool value:" + node.getValue());
        --tab;
    }

    @Override
    public void visit(IntConstNode node) {
        puts("<IntConstNode>");
        ++tab;
        Tools.printTab(tab);
        puts("Int value:" + node.getValue());
        --tab;
    }

    @Override
    public void visit(StringConstNode node) {
        puts("<StringConstNode>");
        ++tab;
        Tools.printTab(tab);
        puts("String value:" + node.getValue());
        --tab;
    }

    @Override
    public void visit(NullConstNode node) {
        puts("<NullConstNode>");
    }

    @Override
    public void visit(TypeNode node) {
        puts("<TypeNode>");
        ++tab;
        Tools.printTab(tab);
        puts("Type: " + node.getType().getTypeName());
        --tab;
    }

    @Override
    public void visit(VarNode node) {
        puts("<VarNode>");
        ++tab;
        Tools.printTab(tab);
        puts("Type:" + node.getType().getType().getTypeName());
        Tools.printTab(tab);
        puts("Identifier: " + node.getIdentifier());
        if (node.getInitExpr() != null) {
            node.getInitExpr().dump(this, tab);
        }
        --tab;
    }

    @Override
    public void visit(VarListNode node) {
        puts("<VarListNode>");
        ++tab;
        for (VarNode varNode : node.getVarNodeList()) {
            varNode.dump(this, tab);
        }
        --tab;
    }
}
