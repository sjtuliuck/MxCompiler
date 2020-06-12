package ir.instruction;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.value.IntValue;
import ir.value.Register;

import java.util.Map;

public class Comparison extends IRInstruction {
    public enum Compare {
        Less, Leq, Greater, Geq, Equal, Neq
    }


    private Register destReg;
    private Compare cmp;
    private IntValue lhs;
    private IntValue rhs;

    public Comparison(BasicBlock basicBlock, Register destReg, Compare cmp, IntValue lhs, IntValue rhs) {
        super(basicBlock);
        this.destReg = destReg;
        this.cmp = cmp;
        this.lhs = lhs;
        this.rhs = rhs;
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
        usedIntValue.add(lhs);
        usedIntValue.add(rhs);
        if (lhs instanceof Register)
            usedRegister.add((Register) lhs);
        if (rhs instanceof Register)
            usedRegister.add((Register) rhs);
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
        if (lhs instanceof Register)
            lhs = regMap.get(lhs);
        if (rhs instanceof Register)
            rhs = regMap.get(rhs);
        reloadUsedValueReg();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        return new Comparison(
                (BasicBlock) renameMap.getOrDefault(getCurBlock(), getCurBlock()),
                (Register) renameMap.getOrDefault(destReg, destReg),
                cmp,
                (IntValue) renameMap.getOrDefault(lhs, lhs),
                (IntValue) renameMap.getOrDefault(rhs, rhs)
        );
    }

    //
    public Register getDestReg() {
        return destReg;
    }

    public void setDestReg(Register destReg) {
        this.destReg = destReg;
    }

    public Compare getCmp() {
        return cmp;
    }

    public void setCmp(Compare cmp) {
        this.cmp = cmp;
    }

    public IntValue getLhs() {
        return lhs;
    }

    public void setLhs(IntValue lhs) {
        this.lhs = lhs;
    }

    public IntValue getRhs() {
        return rhs;
    }

    public void setRhs(IntValue rhs) {
        this.rhs = rhs;
    }
}
