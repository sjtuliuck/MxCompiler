package com.ast;

import com.frontend.ASTPrinter;
import com.utility.CompileError;
import com.utility.Location;
import com.utility.Tools;

public class BinaryExprNode extends ExprNode {
    public enum BinOp {
        mul, div, mod,
        add, sub,
        shiftRight, shiftLeft,
        less, greater, leq, geq,
        neq, equal,
        bitwiseAnd, bitwiseXor, bitwiseOr,
        logicAnd, logicOr,
        assign
    }

    private BinOp op;
    private ExprNode lhs, rhs;

    public BinaryExprNode(Location location, BinOp op, ExprNode lhs, ExprNode rhs) {
        super(location);
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public BinOp getOp() {
        return op;
    }

    public ExprNode getLhs() {
        return lhs;
    }

    public ExprNode getRhs() {
        return rhs;
    }

    public void setOp(String opt) {
        switch (opt) {
            case "*":
                op = BinOp.mul;
                break;
            case "/":
                op = BinOp.div;
                break;
            case "%":
                op = BinOp.mod;
                break;
            case "+":
                op = BinOp.add;
                break;
            case "-":
                op = BinOp.sub;
                break;
            case ">>":
                op = BinOp.shiftRight;
                break;
            case "<<":
                op = BinOp.shiftLeft;
                break;
            case "<":
                op = BinOp.less;
                break;
            case ">":
                op = BinOp.greater;
                break;
            case "<=":
                op = BinOp.leq;
                break;
            case ">=":
                op = BinOp.geq;
                break;
            case "!=":
                op = BinOp.neq;
                break;
            case "==":
                op = BinOp.equal;
                break;
            case "&":
                op = BinOp.bitwiseAnd;
                break;
            case "^":
                op = BinOp.bitwiseXor;
                break;
            case "|":
                op = BinOp.bitwiseOr;
                break;
            case "&&":
                op = BinOp.logicAnd;
                break;
            case "||":
                op = BinOp.logicOr;
                break;
            case "=":
                op = BinOp.assign;
                break;
            default:
                throw new CompileError("bop invalid:" + opt);
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
        return "BinaryExprNode{" +
                "op=" + op +
                ", lhs=" + lhs +
                ", rhs=" + rhs +
                '}';
    }
}
