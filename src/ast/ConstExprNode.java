package ast;

import utility.Location;

abstract public class ConstExprNode extends ExprNode {
    public ConstExprNode(Location location) {
        super(location);
    }
}
