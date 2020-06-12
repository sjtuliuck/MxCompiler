package ir.instruction;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.value.IntValue;
import ir.value.Register;
import utility.CompileError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class IRInstruction {
    protected BasicBlock curBlock;
    private IRInstruction prev = null;
    private IRInstruction next = null;
    boolean removed = false;
    protected List<Register> usedRegister = new ArrayList<>();
    protected List<IntValue> usedIntValue = new ArrayList<>();


    public IRInstruction(BasicBlock curBlock, IRInstruction prev, IRInstruction next) {
        this.curBlock = curBlock;
        this.prev = prev;
        this.next = next;
    }

    public IRInstruction(BasicBlock curBlock) {
        this.curBlock = curBlock;
    }

    public void insertNext(IRInstruction inst) {
        if (next != null) {
            inst.prev = this;
            inst.next = this.next;
            this.next.prev = inst;
            this.next = inst;
        } else {
            this.next = inst;
            inst.prev = this;
            curBlock.setTailInst(inst);
        }
    }

    public void insertPrev(IRInstruction inst) {
        if (prev != null) {
            inst.next = this;
            inst.prev = this.prev;
            this.prev.next = prev;
            this.prev = inst;
        } else {
            this.prev = inst;
            inst.next = this;
            curBlock.setHeadInst(inst);
        }
    }

    public void remove() {
        if (removed)
            throw new CompileError("already removed");

        if (this instanceof BranchInst)
            this.curBlock.removeJumpInst();

        if (prev == null && next == null) {
            curBlock.setHeadInst(null);
            curBlock.setTailInst(null);
        } else if (prev == null) {
            curBlock.setHeadInst(next);
            next.prev = null;
        } else if (next == null) {
            curBlock.setTailInst(prev);
            prev.next = null;
        } else {
            prev.next = next;
            next.prev = prev;
        }
    }

    public abstract void accept(IRVisitor visitor);

    public abstract void reloadUsedValueReg();

    public abstract Register getDefinedRegister();

    public abstract void setDefinedRegister(Register reg);

    public abstract void setUsedRegister(Map<Register, Register> regMap);

    public abstract IRInstruction copyAndRename(Map<Object, Object> renameMap);

    //
    public IRInstruction getPrev() {
        return prev;
    }

    public void setPrev(IRInstruction prev) {
        this.prev = prev;
    }

    public IRInstruction getNext() {
        return next;
    }

    public void setNext(IRInstruction next) {
        this.next = next;
    }

    public BasicBlock getCurBlock() {
        return curBlock;
    }

    public void setCurBlock(BasicBlock curBlock) {
        this.curBlock = curBlock;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public List<Register> getUsedRegister() {
        return usedRegister;
    }

    public void setUsedRegister(List<Register> usedRegister) {
        this.usedRegister = usedRegister;
    }

    public List<IntValue> getUsedIntValue() {
        return usedIntValue;
    }

    public void setUsedIntValue(List<IntValue> usedIntValue) {
        this.usedIntValue = usedIntValue;
    }
}
