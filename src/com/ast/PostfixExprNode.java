package com.ast;

import com.frontend.ASTPrinter;
import com.utility.CompileError;
import com.utility.Location;
import com.utility.Tools;

public class PostfixExprNode extends ExprNode {
    public enum PostOp {
        postInc, postDec
    }

    private PostOp op;
    private ExprNode expr;

    public PostfixExprNode(Location location, PostOp op, ExprNode expr) {
        super(location);
        this.op = op;
        this.expr = expr;
    }

    public PostOp getOp() {
        return op;
    }

    public ExprNode getExpr() {
        return expr;
    }

    public void setOp(String opt) {
        if (opt.equals("++")) {
            op = PostOp.postInc;
        } else if (opt.equals("--")) {
            op = PostOp.postDec;
        } else {
            throw new CompileError("post op invalid");
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void dump(ASTPrinter printer, int tab) {
        Tools.printTab(tab);
        printer.visit(this);
    }

    @Override
    public String toString() {
        return "PostfixExprNode{" +
                "op=" + op +
                ", expr=" + expr +
                '}';
    }
}
