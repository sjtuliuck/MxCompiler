package com.ir;

import com.ir.instruction.*;
import com.ir.value.*;

public interface IRVisitor {
    void visit(BasicBlock node);
    void visit(IRFunction node);
    void visit(IRRoot node);
    //
    void visit(BinaryOperation node);
    void visit(UnaryOperation node);
    void visit(Comparison node);
    void visit(Immediate node);
    void visit(FuncCall node);
    //
    void visit(Branch node);
    void visit(Return node);
    void visit(Jump node);
    //
    void visit(HeapAllocate node);
    void visit(Load node);
    void visit(Store node);
    void visit(Move node);
    //
    void visit(StackSlot node);
    void visit(PReg node);
    void visit(VReg node);
    void visit(StaticStr node);
    void visit(StaticVar node);

}
