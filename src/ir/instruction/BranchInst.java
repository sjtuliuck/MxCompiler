package ir.instruction;

import ir.BasicBlock;

import java.util.Map;

public abstract class BranchInst extends IRInstruction {
    public BranchInst(BasicBlock basicBlock, IRInstruction prev, IRInstruction next) {
        super(basicBlock, prev, next);

    }

    protected BranchInst(BasicBlock basicBlock) {
        super(basicBlock);
    }

    @Override
    public abstract BranchInst copyAndRename(Map<Object, Object> renameMap);
}
