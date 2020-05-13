package com.ir.instruction;

import com.ir.BasicBlock;
import com.ir.IRVisitor;
import com.ir.value.IntValue;
import com.ir.value.Register;
import com.ir.value.StackSlot;

import java.util.Map;

public class Store extends IRInstruction {
    private IntValue value;
    private IntValue addr;
    private int size;
    private int offset;
    private boolean isStaticData;

    public Store(BasicBlock basicBlock, IntValue value, IntValue addr, int size, int offset) {
        super(basicBlock);
        this.value = value;
        this.addr = addr;
        this.size = size;
        this.offset = offset;
        reloadUsedValueReg();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void reloadUsedValueReg() {
        usedIntValue.clear();
        usedRegister.clear();
        usedIntValue.add(value);
        usedIntValue.add(addr);
        if (value instanceof Register)
            usedRegister.add((Register) value);
        if (addr instanceof  Register && !(addr instanceof StackSlot))
            usedRegister.add((Register) addr);
    }

    @Override
    public Register getDefinedRegister() {
        return null;
    }

    @Override
    public void setDefinedRegister(Register reg) {
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (value instanceof Register)
            value = regMap.get(value);
        if (addr instanceof  Register && !(addr instanceof StackSlot))
            addr = regMap.get(addr);
        reloadUsedValueReg();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        // todo
        return null;
    }

    //

    public IntValue getValue() {
        return value;
    }

    public void setValue(IntValue value) {
        this.value = value;
    }

    public IntValue getAddr() {
        return addr;
    }

    public void setAddr(IntValue addr) {
        this.addr = addr;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isStaticData() {
        return isStaticData;
    }

    public void setStaticData(boolean staticData) {
        isStaticData = staticData;
    }
}
