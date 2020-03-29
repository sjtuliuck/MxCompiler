package com.ast;

import java.util.*;
import com.utility.*;

public class ProgramNode extends Node {
    List<DefNode> defNodeList;

    public ProgramNode(Location location, List<DefNode> defNodeList) {
        super(location);
        this.defNodeList = defNodeList;
    }

    public List<DefNode> getDefNodeList() {
        return defNodeList;
    }

    public void addDefNode(DefNode node) {
        defNodeList.add(node);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
