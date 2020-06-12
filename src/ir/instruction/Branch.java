package ir.instruction;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.value.IntValue;
import ir.value.Register;

import java.util.Map;

public class Branch extends BranchInst {
    private IntValue cond;
    private BasicBlock thenBlock, elseBlock;

    public Branch(BasicBlock basicBlock, IntValue cond, BasicBlock thenBlock, BasicBlock elseBlock) {
        super(basicBlock);
        this.cond = cond;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
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
        usedIntValue.add(cond);
        if (cond instanceof Register) {
            usedRegister.add((Register) cond);
        }
    }

    @Override
    public void setUsedRegister(Map<Register, Register> map) {
        if (cond instanceof Register) {
            cond = map.get(cond);
        }
        reloadUsedValueReg();
    }

    @Override
    public Branch copyAndRename(Map<Object, Object> renameMap) {
        return new Branch(
                (BasicBlock) renameMap.getOrDefault(getCurBlock(), getCurBlock()),
                (IntValue) renameMap.getOrDefault(cond, cond),
                (BasicBlock) renameMap.getOrDefault(thenBlock, thenBlock),
                (BasicBlock) renameMap.getOrDefault(elseBlock, elseBlock)
        );
    }

    @Override
    public Register getDefinedRegister() {
        return null;
    }

    @Override
    public void setDefinedRegister(Register reg) {
    }

    //

    public IntValue getCond() {
        return cond;
    }

    public void setCond(IntValue cond) {
        this.cond = cond;
    }

    public BasicBlock getThenBlock() {
        return thenBlock;
    }

    public void setThenBlock(BasicBlock thenBlock) {
        this.thenBlock = thenBlock;
    }

    public BasicBlock getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(BasicBlock elseBlock) {
        this.elseBlock = elseBlock;
    }
}
