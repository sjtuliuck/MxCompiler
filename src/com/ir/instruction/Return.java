package com.ir.instruction;

import com.ir.BasicBlock;
import com.ir.IRVisitor;
import com.ir.value.*;

import java.util.Map;

public class Return extends BranchInst {
    private IntValue retValue;

    public Return(BasicBlock basicBlock, IntValue retValue) {
        super(basicBlock);
        this.retValue = retValue;
        reloadUsedValueReg();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void reloadUsedValueReg() {
        usedRegister.clear();
        usedIntValue.clear();
        if (retValue != null) {
            usedIntValue.add(retValue);
        }
        if (retValue != null && retValue instanceof Register) {
            usedRegister.add((Register) retValue);
        }
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
        if (retValue instanceof Register)
            retValue = regMap.get(retValue);
        reloadUsedValueReg();
    }

    @Override
    public BranchInst copyAndRename(Map<Object, Object> renameMap) {
        return new Return(
                (BasicBlock) renameMap.getOrDefault(getCurBlock(), getCurBlock()),
                (IntValue) renameMap.getOrDefault(retValue, retValue)
        );
    }

    //

    public IntValue getRetValue() {
        return retValue;
    }

    public void setRetValue(IntValue retValue) {
        this.retValue = retValue;
    }
}
