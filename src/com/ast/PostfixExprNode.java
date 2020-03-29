package com.ast;

import com.utility.Location;

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
        if (opt == "++") {
            op = PostOp.postInc;
        } else if (opt == "--") {
            op = PostOp.postDec;
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
