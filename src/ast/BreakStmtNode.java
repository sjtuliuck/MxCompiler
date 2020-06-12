package ast;

import frontend.ASTPrinter;
import utility.Location;
import utility.Tools;

public class BreakStmtNode extends StmtNode {
    public BreakStmtNode(Location location) {
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

    @Override
    public String toString() {
        return super.toString();
    }
}
