package com.ast;

import com.frontend.ASTPrinter;
import com.frontend.type.Type;
import com.utility.Location;
import com.utility.Tools;

import java.util.List;

public class NewExprNode extends ExprNode {
    private Type newType;
    private List<ExprNode> exprNodeList;
    private int dim;

    public NewExprNode(Location location, Type newType, List<ExprNode> exprNodeList, int dim) {
        super(location);
        this.newType = newType;
        this.exprNodeList = exprNodeList;
        this.dim = dim;
    }

    public Type getNewType() {
        return newType;
    }

    public List<ExprNode> getExprNodeList() {
        return exprNodeList;
    }

    public int getDim() {
        return dim;
    }

    public void setNewType(Type newType) {
        this.newType = newType;
    }

    public void setExprNodeList(List<ExprNode> exprNodeList) {
        this.exprNodeList = exprNodeList;
    }

    public void setDim(int dim) {
        this.dim = dim;
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
        return "NewExprNode{" +
                "newType=" + newType +
                ", exprNodeList=" + exprNodeList +
                ", dim=" + dim +
                '}';
    }
}
