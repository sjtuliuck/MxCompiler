package com.ir.instruction;

import com.ir.BasicBlock;
import com.ir.IRVisitor;
import com.ir.value.IntValue;
import com.ir.value.Register;

import java.util.Map;

public class UnaryOperation extends IRInstruction {
    public enum UnaryOp {
        Neg, BitwiseNot
    }

    private Register destReg;
    private UnaryOp op;
    private IntValue operand;

    public UnaryOperation(BasicBlock basicBlock, Register destReg, UnaryOp op, IntValue operand) {
        super(basicBlock);
        this.destReg = destReg;
        this.op = op;
        this.operand = operand;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void reloadUsedValueReg() {
        usedIntValue.clear();
        usedRegister.clear();
        usedIntValue.add(operand);
        if (operand instanceof Register)
            usedRegister.add((Register) operand);
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
        if (operand instanceof Register)
            operand = regMap.get(operand);
        reloadUsedValueReg();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        return new UnaryOperation(
                (BasicBlock) renameMap.getOrDefault(getCurBlock(), getCurBlock()),
                (Register) renameMap.getOrDefault(destReg, destReg),
                op,
                (IntValue) renameMap.getOrDefault(operand, operand)
        );
    }

    //

    public Register getDestReg() {
        return destReg;
    }

    public void setDestReg(Register destReg) {
        this.destReg = destReg;
    }

    public UnaryOp getOp() {
        return op;
    }

    public void setOp(UnaryOp op) {
        this.op = op;
    }

    public IntValue getOperand() {
        return operand;
    }

    public void setOperand(IntValue operand) {
        this.operand = operand;
    }
}
