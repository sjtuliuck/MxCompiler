package com.frontend;

import com.ast.*;
import com.frontend.type.*;
import com.parser.MxStarBaseVisitor;
import com.parser.MxStarParser;
import com.parser.MxStarVisitor;
import com.utility.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;

public class ASTBuilder extends MxStarBaseVisitor<Node> {
    private ErrorHandler errorHandler;
    // type
    public Type boolType = new BoolType();
    public Type intType = new IntType();
    public Type nullType = new NullType();
    public Type stringType = new StringType();
    public Type voidType = new VoidType();

    public ASTBuilder(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public Node visitProgram(MxStarParser.ProgramContext ctx) {
        Location location = new Location(ctx);
        List<DefNode> defNodeList = new ArrayList<>();
        for (ParserRuleContext def : ctx.def()) {
            if (def != null) {
                defNodeList.add((DefNode) visit(def));
            }
        }
        return new ProgramNode(location, defNodeList);
    }

    @Override
    public Node visitDef(MxStarParser.DefContext ctx) {
        Location location = new Location(ctx);
        if (ctx.varDef() != null) {
            return visit(ctx.varDef());
        } else if (ctx.classDef() != null) {
            return visit(ctx.classDef());
        } else if (ctx.funcDef() != null) {
            return visit(ctx.funcDef());
        } else {
            return null;
        }
    }

    // varDef: type varList ';';
    @Override
    public Node visitVarDef(MxStarParser.VarDefContext ctx) {
        Location location = new Location(ctx);
        TypeNode type = (TypeNode) visit(ctx.type());
        VarListNode varListNode = ((VarListNode) visit(ctx.varList()));
        List<VarNode> varNodes = varListNode.getVarNodeList();
        for (var varNode : varNodes) {
            varNode.setType(type);
        }
        return new VarDefNode(location, varNodes);
    }

    @Override
    public Node visitClassDef(MxStarParser.ClassDefContext ctx) {
        Location location = new Location(ctx);
        String identifier = ctx.Identifier().getText();
        // variable
        List<VarNode> varNodeList = new ArrayList<>();
        for (var varDef : ctx.varDef()) {
            VarDefNode varDefNode = (VarDefNode) visit(varDef);
            varNodeList.addAll(varDefNode.getVarNodeList());
        }
        // function
        List<FuncDefNode> funcDefNodeList = new ArrayList<>();
        for (var funcDef : ctx.funcDef()) {
            FuncDefNode funcDefNode = (FuncDefNode) visit(funcDef);
            if (funcDefNode.getIdentifier().equals(identifier)) {
                throw new CompileError(new Location(funcDef), "constructor with ret type");
            }
            funcDefNodeList.add(funcDefNode);
        }
        // constructor
        if (ctx.constructorDef().size() > 1) {
            throw new CompileError(new Location(ctx.constructorDef(0)), "constructor number error");
        }
        for (var constructorDef : ctx.constructorDef()) {
            if (!constructorDef.Identifier().getText().equals(identifier)) {
                throw new CompileError(new Location(constructorDef), "constructor name error");
            }
            funcDefNodeList.add((FuncDefNode) visit(constructorDef));
        }
        return new ClassDefNode(location, identifier, varNodeList, funcDefNodeList);
    }

    @Override
    public Node visitFuncDef(MxStarParser.FuncDefContext ctx) {
        Location location = new Location(ctx);
        // type
        TypeNode retType;
        if (ctx.type() != null) {
            retType = (TypeNode) visit(ctx.type());
        } else {
            retType = new TypeNode(new Location(ctx.Void().getSymbol()), voidType);
        }
        // identifier
        String identifier = ctx.Identifier().getText();
        // param
        List<VarNode> paramList = null;
        if (ctx.paramList() != null) {
            VarListNode varListNode = (VarListNode) visit(ctx.paramList());
            paramList = varListNode.getVarNodeList();
        }
        StmtNode stmt = (StmtNode) visit(ctx.block());
        return new FuncDefNode(location, retType, identifier, paramList, stmt);
    }

    @Override
    public Node visitConstructorDef(MxStarParser.ConstructorDefContext ctx) {
        Location location = new Location(ctx);
        // type
        TypeNode retType = new TypeNode(location, nullType);
        // identifier
        String identifier = ctx.Identifier().getText();
        // param
        List<VarNode> paramList = null;
        if (ctx.paramList() != null) {
            VarListNode varListNode = (VarListNode) visit(ctx.paramList());
            paramList = varListNode.getVarNodeList();
        }
        StmtNode stmt = (StmtNode) visit(ctx.block());
        return new FuncDefNode(location, retType, identifier, paramList, stmt);
    }


    @Override
    public Node visitVarList(MxStarParser.VarListContext ctx) {
        Location location = new Location(ctx);
        List<VarNode> varNodeList = new ArrayList<>();
        for (var variable : ctx.var()) {
            VarNode varNode = (VarNode) visit(variable);
            varNodeList.add(varNode);
        }
        return new VarListNode(location, varNodeList);
    }

    @Override
    public Node visitVar(MxStarParser.VarContext ctx) {
        Location location = new Location(ctx);
        TypeNode type = new TypeNode(location, nullType);
        String identifier = ctx.Identifier().getText();
        ExprNode initExpr = null;
        if (ctx.expr() != null) {
            initExpr = (ExprNode) visit(ctx.expr());
        }
        return new VarNode(location, type, identifier, initExpr);
    }

    // paramList: param (',' param)*;
    // param: type Identifier;
    @Override
    public Node visitParamList(MxStarParser.ParamListContext ctx) {
        Location location = new Location(ctx);
        List<VarNode> varNodeList = new ArrayList<>();
        for (var param : ctx.param()) {
            VarNode varNode = (VarNode) visit(param);
            varNodeList.add(varNode);
        }
        return new VarListNode(location, varNodeList);
    }

    @Override
    public Node visitParam(MxStarParser.ParamContext ctx) {
        Location location = new Location(ctx);
        TypeNode type = (TypeNode) visit(ctx.type());
        String identifier = ctx.Identifier().getText();
        return new VarNode(location, type, identifier, null);
    }

    @Override
    public Node visitType(MxStarParser.TypeContext ctx) {
        Location location = new Location(ctx);
        if (ctx.nonArrayType() != null) {
            return visit(ctx.nonArrayType());
        } else {
            TypeNode arrayType = (TypeNode) visit(ctx.type());
            return new TypeNode(location, new ArrayType(arrayType.getType()));
        }
    }

    @Override
    public Node visitNonArrayType(MxStarParser.NonArrayTypeContext ctx) {
        Location location = new Location(ctx);
        if (ctx.Bool() != null) {
            return new TypeNode(location, boolType);
        } else if (ctx.Int() != null) {
            return new TypeNode(location, intType);
        } else if (ctx.String() != null) {
            return new TypeNode(location, stringType);
        } else {
            Type classType = new ClassType(ctx.Identifier().getText());
            return new TypeNode(location, classType);
        }
    }

    @Override
    public Node visitBlock(MxStarParser.BlockContext ctx) {
        Location location = new Location(ctx);
        List<StmtNode> stmtNodeList = new ArrayList<>();
        for (var stmt : ctx.stmt()) {
            StmtNode stmtNode = (StmtNode) visit(stmt);
            if (stmtNode != null) {
                stmtNodeList.add(stmtNode);
            }
        }
        return new BlockStmtNode(location, stmtNodeList);
    }

    @Override
    public Node visitVarDefStmt(MxStarParser.VarDefStmtContext ctx) {
        Location location = new Location(ctx);
        VarDefNode varDefNode = (VarDefNode) visit(ctx.varDef());
        List<VarNode> varNodeList = varDefNode.getVarNodeList();
        return new VarDefStmtNode(location, varNodeList);
    }


    @Override
    public Node visitExprStmt(MxStarParser.ExprStmtContext ctx) {
        Location location = new Location(ctx);
        ExprNode exprNode = (ExprNode) visit(ctx.expr());
        return new ExprStmtNode(location, exprNode);
    }

    @Override
    public Node visitIfStmt(MxStarParser.IfStmtContext ctx) {
        Location location = new Location(ctx);
        ExprNode condExpr = (ExprNode) visit(ctx.expr());
        StmtNode thenStmt = (StmtNode) visit(ctx.thenStmt);
        StmtNode elseStmt = null;
        if (ctx.elseStmt != null) {
            elseStmt = (StmtNode) visit(ctx.elseStmt);
        }
        return new IfStmtNode(location, condExpr, thenStmt, elseStmt);
    }

    @Override
    public Node visitWhileStmt(MxStarParser.WhileStmtContext ctx) {
        Location location = new Location(ctx);
        ExprNode condExpr = (ExprNode) visit(ctx.expr());
        StmtNode bodyStmt = (StmtNode) visit(ctx.stmt());
        return new WhileStmtNode(location, condExpr, bodyStmt);
    }

    @Override
    public Node visitForStmt(MxStarParser.ForStmtContext ctx) {
        Location location = new Location(ctx);
        ExprNode initExpr = (ctx.init != null) ? (ExprNode) visit(ctx.init) : null;
        ExprNode condExpr = (ctx.cond != null) ? (ExprNode) visit(ctx.cond) : null;
        ExprNode stepExpr = (ctx.step != null) ? (ExprNode) visit(ctx.step) : null;
        StmtNode body = (StmtNode) visit(ctx.stmt());
        return new ForStmtNode(location, initExpr, condExpr, stepExpr, body);
    }

    @Override
    public Node visitReturnStmt(MxStarParser.ReturnStmtContext ctx) {
        Location location = new Location(ctx);
        ExprNode retExpr = (ctx.expr() != null) ? (ExprNode) visit(ctx.expr()) : null;
        return new ReturnStmtNode(location, retExpr);
    }

    @Override
    public Node visitBreakStmt(MxStarParser.BreakStmtContext ctx) {
        Location location = new Location(ctx);
        return new BreakStmtNode(location);
    }

    @Override
    public Node visitContinueStmt(MxStarParser.ContinueStmtContext ctx) {
        Location location = new Location(ctx);
        return new ContinueStmtNode(location);
    }

    @Override
    public Node visitBlockStmt(MxStarParser.BlockStmtContext ctx) {
        return visit(ctx.block());
    }

    @Override
    public Node visitEmptyStmt(MxStarParser.EmptyStmtContext ctx) {
        return null;
    }

    @Override
    public Node visitExprList(MxStarParser.ExprListContext ctx) {
        Location location = new Location(ctx);
        List<ExprNode> exprNodeList = new ArrayList<>();
        for (var expr : ctx.expr()) {
            ExprNode exprNode = (ExprNode) visit(expr);
            exprNodeList.add(exprNode);
        }
        return new ExprListNode(location, exprNodeList);
    }

    @Override
    public Node visitNewExpr(MxStarParser.NewExprContext ctx) {
        return visit(ctx.creator());
    }

    @Override
    public Node visitThisExpr(MxStarParser.ThisExprContext ctx) {
        Location location = new Location(ctx);
        return new ThisExprNode(location);
    }

    @Override
    public Node visitPrefixExpr(MxStarParser.PrefixExprContext ctx) {
        Location location = new Location(ctx);
        ExprNode expr = (ExprNode) visit(ctx.expr());
        PrefixExprNode prefixExprNode = new PrefixExprNode(location, null, expr);
        String op = ctx.op.getText();
        prefixExprNode.setOp(op);
        return prefixExprNode;
    }

    @Override
    public Node visitFuncExpr(MxStarParser.FuncExprContext ctx) {
        Location location = new Location(ctx);
        ExprNode exprNode = (ExprNode) visit(ctx.expr());
        FuncExprNode funcExprNode = new FuncExprNode(location, exprNode, null);
        if (ctx.exprList() != null) {
            ExprListNode exprListNode = (ExprListNode) visit(ctx.exprList());
            funcExprNode.setParamList(exprListNode.getExprList());
        }
        return funcExprNode;
    }

    @Override
    public Node visitArrayExpr(MxStarParser.ArrayExprContext ctx) {
        Location location = new Location(ctx);
        ExprNode array = (ExprNode) visit(ctx.array);
        ExprNode idx = (ExprNode) visit(ctx.idx);
        return new ArrayExprNode(location, array, idx);
    }

    @Override
    public Node visitMemberExpr(MxStarParser.MemberExprContext ctx) {
        Location location = new Location(ctx);
        ExprNode expr = (ExprNode) visit(ctx.expr());
        String identifier = ctx.Identifier().getText();
        return new MemberExprNode(location, expr, identifier);
    }

    @Override
    public Node visitBinaryExpr(MxStarParser.BinaryExprContext ctx) {
        Location location = new Location(ctx);
        ExprNode lhs = (ExprNode) visit(ctx.lhs);
        ExprNode rhs = (ExprNode) visit(ctx.rhs);
        BinaryExprNode binaryExprNode = new BinaryExprNode(location, null, lhs, rhs);
        String bop = ctx.bop.getText();
//        System.out.println(location.toString() + bop);
        binaryExprNode.setOp(bop);
        return binaryExprNode;
    }

    @Override
    public Node visitPostfixExpr(MxStarParser.PostfixExprContext ctx) {
        Location location = new Location(ctx);
        ExprNode expr = (ExprNode) visit(ctx.expr());
        PostfixExprNode postfixExprNode = new PostfixExprNode(location, null, expr);
        String op = ctx.op.getText();
        postfixExprNode.setOp(op);
        return postfixExprNode;
    }

    @Override
    public Node visitParenExpr(MxStarParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Node visitIdentifierExpr(MxStarParser.IdentifierExprContext ctx) {
        Location location = new Location(ctx);
        String identifier = ctx.Identifier().getText();
        return new IdentifierExprNode(location, identifier);
    }

    @Override
    public Node visitConstExpr(MxStarParser.ConstExprContext ctx) {
        return visit(ctx.constant());
    }

    @Override
    public Node visitConstant(MxStarParser.ConstantContext ctx) {
        Location location = new Location(ctx);
        String value = ctx.getText();
        if (ctx.BoolConst() != null) {
            return new BoolConstNode(location, value.equals("true"));
        } else if (ctx.IntConst() != null) {
            return new IntConstNode(location, Integer.parseInt(value));
        } else if (ctx.StringConst() != null) {
            return new StringConstNode(location, value);
        } else {
            return new NullConstNode(location);
        }
    }

    @Override
    public Node visitErrorCreator(MxStarParser.ErrorCreatorContext ctx) {
        Location location = new Location(ctx);
        throw new CompileError(location, "Creator Error");
    }

    @Override
    public Node visitArrayCreator(MxStarParser.ArrayCreatorContext ctx) {
        Location location = new Location(ctx);
        Type newType = ((TypeNode) visit(ctx.nonArrayType())).getType();
        int dim = 0;
//        System.out.println(ctx.children.size());
        for (var child : ctx.children) {
//            System.out.println(child.getText());
            if (child.getText().equals("[") || child.getText().equals("[]")) {
                ++dim;
            }
        }
//        System.out.println(dim);
        List<ExprNode> exprNodeList = new ArrayList<>();
        for (var expr : ctx.expr()) {
            ExprNode exprNode = (ExprNode) visit(expr);
            exprNodeList.add(exprNode);
        }
        for (int i = 0; i < dim; ++i) {
            newType = new ArrayType(newType);
        }
        return new NewExprNode(location, newType, exprNodeList, dim);
    }

    @Override
    public Node visitNonArrayCreator(MxStarParser.NonArrayCreatorContext ctx) {
        Location location = new Location(ctx);
        Type newType = ((TypeNode) visit(ctx.nonArrayType())).getType();
        return new NewExprNode(location, newType, null, 0);
    }

}

