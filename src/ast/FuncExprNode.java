package ast;

import frontend.ASTPrinter;
import frontend.entity.FuncEntity;
import utility.Location;
import utility.Tools;

import java.util.List;

public class FuncExprNode extends ExprNode {
    private ExprNode expr;
    private List<ExprNode> paramList;
    FuncEntity funcEntity;

    public FuncExprNode(Location location, ExprNode expr, List<ExprNode> paramList) {
        super(location);
        this.expr = expr;
        this.paramList = paramList;
    }

    public ExprNode getExpr() {
        return expr;
    }

    public List<ExprNode> getParamList() {
        return paramList;
    }

    public FuncEntity getFuncEntity() {
        return funcEntity;
    }

    public void setExpr(ExprNode expr) {
        this.expr = expr;
    }

    public void setParamList(List<ExprNode> paramList) {
        this.paramList = paramList;
    }

    public void setFuncEntity(FuncEntity funcEntity) {
        this.funcEntity = funcEntity;
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
        return "FuncExprNode{" +
                "expr=" + expr +
                ", paramList=" + paramList +
                '}';
    }
}
