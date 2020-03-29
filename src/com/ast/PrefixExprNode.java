package com.ast;

import com.utility.Location;

public class PrefixExprNode extends ExprNode {
    public enum PreOp {
        preInc, preDec, signPos, signNeg, bitwiseNot, logicNot
    }

    private PreOp op;
    private ExprNode expr;

    public PrefixExprNode(Location location, PreOp op, ExprNode expr) {
        super(location);
        this.op = op;
        this.expr = expr;
    }

    public PreOp getOp() {
        return op;
    }

    public ExprNode getExpr() {
        return expr;
    }

    public void setOp(String opt) {
        switch (opt) {
            case "++":
                op = PreOp.preInc;
                break;
            case "--":
                op = PreOp.preDec;
                break;
            case "+":
                op = PreOp.signPos;
                break;
            case "-":
                op = PreOp.signNeg;
                break;
            case "~":
                op = PreOp.bitwiseNot;
                break;
            case "!":
                op = PreOp.logicNot;
                break;
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
