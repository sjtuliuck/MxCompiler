package ir.instruction;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.value.IntValue;
import ir.value.Register;

import java.util.Map;

public class Move extends IRInstruction {
    private Register destReg;
    private IntValue src;

    public Move(BasicBlock basicBlock, Register destReg, IntValue src) {
        super(basicBlock);
        this.destReg = destReg;
        this.src = src;
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
        usedIntValue.add(src);
        if (src instanceof Register)
            usedRegister.add((Register) src);
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
        if (src instanceof Register)
            src = regMap.get(src);
        reloadUsedValueReg();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        return new Move(
                (BasicBlock) renameMap.getOrDefault(getCurBlock(), getCurBlock()),
                (Register) renameMap.getOrDefault(destReg, destReg),
                (IntValue) renameMap.getOrDefault(src, src)
        );
    }

    //

    public Register getDestReg() {
        return destReg;
    }

    public void setDestReg(Register destReg) {
        this.destReg = destReg;
    }

    public IntValue getSrc() {
        return src;
    }

    public void setSrc(IntValue src) {
        this.src = src;
    }
}
