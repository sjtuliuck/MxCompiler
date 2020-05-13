package com.ir.instruction;

import com.ir.BasicBlock;
import com.ir.IRVisitor;
import com.ir.value.Register;

import java.util.Map;

public class Jump extends BranchInst {
    private BasicBlock targetBlock;

    public Jump(BasicBlock basicBlock, BasicBlock targetBlock) {
        super(basicBlock);
        this.targetBlock = targetBlock;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void reloadUsedValueReg() {
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
    }

    @Override
    public BranchInst copyAndRename(Map<Object, Object> renameMap) {
        return new Jump(
                (BasicBlock) renameMap.getOrDefault(getCurBlock(), getCurBlock()),
                (BasicBlock) renameMap.getOrDefault(targetBlock, targetBlock)
        );
    }

    //
    public BasicBlock getTargetBlock() {
        return targetBlock;
    }

    public void setTargetBlock(BasicBlock targetBlock) {
        this.targetBlock = targetBlock;
    }
}
