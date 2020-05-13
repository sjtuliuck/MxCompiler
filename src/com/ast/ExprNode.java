package com.ast;

import com.frontend.type.Type;
import com.ir.BasicBlock;
import com.ir.value.IntValue;
import com.utility.*;

abstract public class ExprNode extends Node {
    protected Type type;
    protected boolean lvalue;

    private BasicBlock trueBlock = null;
    private BasicBlock falseBlock = null;
    private IntValue addr = null;
    private IntValue reg = null;
    private int offset;

    public ExprNode(Location location) {
        super(location);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isLvalue() {
        return lvalue;
    }

    public void setLvalue(boolean lvalue) {
        this.lvalue = lvalue;
    }

    public BasicBlock getTrueBlock() {
        return trueBlock;
    }

    public void setTrueBlock(BasicBlock trueBlock) {
        this.trueBlock = trueBlock;
    }

    public BasicBlock getFalseBlock() {
        return falseBlock;
    }

    public void setFalseBlock(BasicBlock falseBlock) {
        this.falseBlock = falseBlock;
    }

    public IntValue getAddr() {
        return addr;
    }

    public void setAddr(IntValue addr) {
        this.addr = addr;
    }

    public IntValue getReg() {
        return reg;
    }

    public void setReg(IntValue reg) {
        this.reg = reg;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
