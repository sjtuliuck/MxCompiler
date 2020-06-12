package ast;

import frontend.ASTPrinter;
import utility.Location;
import utility.Tools;

public class IntConstNode extends ConstExprNode {
    private int value;

    public IntConstNode(Location location, int value) {
        super(location);
        this.value = value;
    }

    public int getValue() {
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
        return "IntConstNode{" +
                "value=" + value +
                '}';
    }
}
