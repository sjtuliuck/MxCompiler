package ir.instruction;

import ir.BasicBlock;
import ir.IRFunction;
import ir.IRVisitor;
import ir.value.IntValue;
import ir.value.Register;
import utility.CompileError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FuncCall extends IRInstruction {
    private Register retReg;
    private IRFunction function;
    private List<IntValue> params = new ArrayList<>();

    public FuncCall(BasicBlock basicBlock, Register retReg, IRFunction function, List<IntValue> params) {
        super(basicBlock);
        this.retReg = retReg;
        this.function = function;
        this.params = params;
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
        for (IntValue param : params) {
            if (param instanceof Register) {
                usedRegister.add((Register) param);
            }
            usedIntValue.add(param);
        }
    }

    @Override
    public void setUsedRegister(Map<Register, Register> regMap) {
        for (IntValue param : params) {
                if (param instanceof Register) {
                    if (!Collections.replaceAll(params, param, regMap.get(param))) {
                        throw new CompileError("FuncCall set used Register error");
                    }
                }
        }
        reloadUsedValueReg();
    }

    @Override
    public Register getDefinedRegister() {
        return retReg;
    }

    @Override
    public void setDefinedRegister(Register reg) {
        this.retReg = reg;
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap) {
        List<IntValue> newParams = new ArrayList<>();
        for (IntValue param : params) {
            newParams.add((IntValue) renameMap.getOrDefault(param, param));
        }
        return new FuncCall(
                (BasicBlock) renameMap.getOrDefault(getCurBlock(), getCurBlock()),
                (Register) renameMap.getOrDefault(retReg, retReg),
                function,
                newParams
        );
    }

    //
    public Register getRetReg() {
        return retReg;
    }

    public void setRetReg(Register retReg) {
        this.retReg = retReg;
    }

    public IRFunction getFunction() {
        return function;
    }

    public void setFunction(IRFunction function) {
        this.function = function;
    }

    public List<IntValue> getParams() {
        return params;
    }

    public void setParams(List<IntValue> params) {
        this.params = params;
    }
}
