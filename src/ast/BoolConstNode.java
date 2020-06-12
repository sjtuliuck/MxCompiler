package ast;

import frontend.ASTPrinter;
import utility.Location;
import utility.Tools;

public class BoolConstNode extends ConstExprNode {
    private boolean value;

    public BoolConstNode(Location location, boolean value) {
        super(location);
        this.value = value;
    }

    public boolean getValue() {
        return value;
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
        return "BoolConstNode{" +
                "value=" + value +
                '}';
    }
}
