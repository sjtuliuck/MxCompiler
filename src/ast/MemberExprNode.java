package ast;

import frontend.ASTPrinter;
import utility.Location;
import utility.Tools;

public class MemberExprNode extends ExprNode {
    private ExprNode expr;
    private String identifier;

    public MemberExprNode(Location location, ExprNode expr, String identifier) {
        super(location);
        this.expr = expr;
        this.identifier = identifier;
    }

    public ExprNode getExpr() {
        return expr;
    }

    public String getIdentifier() {
        return identifier;
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
        return "MemberExprNode{" +
                "expr=" + expr +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
