package ast;

import utility.Location;

abstract public class StmtNode extends Node {
    public StmtNode(Location location) {
        super(location);
    }
}
