package ast;

import frontend.ASTPrinter;
import utility.Location;
import utility.Tools;

public class NullConstNode extends ConstExprNode {
    public NullConstNode(Location location) {
        super(location);
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
}
