package com.ir.instruction;

import com.ir.BasicBlock;
import com.ir.IRVisitor;
import com.ir.value.IntValue;
import com.ir.value.Register;

import java.util.Map;

public class HeapAllocate extends IRInstruction {
    private Register destReg;
    private IntValue allocSize;

    public HeapAllocate(BasicBlock basicBlock, Register destReg, IntValue allocSize) {
        super(basicBlock);
        this.destReg = destReg;
        this.allocSize = allocSize;
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
        usedIntValue.add(allocSize);
        if (allocSize instanceof Register)
            usedRegister.add((Register) allocSize);
    }

    @Override
    public Register getDefinedRegister() {
        return destReg;
    }

    @Override
    public void setDefinedRegister(Register reg) {
        this.destReg = reg;
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        if (allocSize instanceof Register) {
            allocSize = regMap.get(allocSize);
        }
        reloadUsedValueReg();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        return new HeapAllocate(
                (BasicBlock) renameMap.getOrDefault(getCurBlock(), getCurBlock()),
                (Register) renameMap.getOrDefault(destReg, destReg),
                (IntValue) renameMap.getOrDefault(allocSize, allocSize)
        );
    }

    //

    public Register getDestReg() {
        return destReg;
    }

    public void setDestReg(Register destReg) {
        this.destReg = destReg;
    }

    public IntValue getAllocSize() {
        return allocSize;
    }

    public void setAllocSize(IntValue allocSize) {
        this.allocSize = allocSize;
    }
}
