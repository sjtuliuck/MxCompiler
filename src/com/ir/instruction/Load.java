package com.ir.instruction;

import com.ir.BasicBlock;
import com.ir.IRVisitor;
import com.ir.value.IntValue;
import com.ir.value.Register;
import com.ir.value.StackSlot;

import java.util.Map;

public class Load extends IRInstruction {
    private Register destReg;
    private IntValue addr;
    private int size;
    private int offset;
    private boolean isStaticData;
    private boolean isLoadAddr;

    public Load(BasicBlock basicBlock, Register destReg, IntValue addr, int size, int offset) {
        super(basicBlock);
        this.destReg = destReg;
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
        usedIntValue.add(addr);
        if (addr instanceof Register && !(addr instanceof StackSlot))
            usedIntValue.add((Register) addr);
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
        if (addr instanceof Register && !(addr instanceof StackSlot))
            addr = regMap.get(addr);
        reloadUsedValueReg();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        // todo
        return null;
    }

    //
    public Register getDestReg() {
        return destReg;
    }

    public void setDestReg(Register destReg) {
        this.destReg = destReg;
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

    public boolean isLoadAddr() {
        return isLoadAddr;
    }

    public void setLoadAddr(boolean loadAddr) {
        isLoadAddr = loadAddr;
    }
}
