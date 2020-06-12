package ir;

import ast.ForStmtNode;
import ir.instruction.*;
import utility.CompileError;

import java.util.HashSet;
import java.util.Set;

public class BasicBlock {
    private IRFunction function;

    private String name;

    private IRInstruction headInst = null;
    private IRInstruction tailInst = null;

    private Set<BasicBlock> prevBlocks = new HashSet<>();
    private Set<BasicBlock> nextBlocks = new HashSet<>();

    private boolean end = false;

    private ForStmtNode forStmtNode;

    private static int cnt = 0;
    private int blockNum;

    private int preOrderIndex;
    private int postOrderIndex;

    public BasicBlock(IRFunction function, String name) {
        this.function = function;
        this.name = name;
        this.blockNum = cnt++;
    }

    public void append(IRInstruction inst) {
        if (end) {
            throw new CompileError("Fail to append inst, the block ends");
        }
        if (tailInst != null) {
            tailInst.insertNext(inst);
        }
    }

    public void addNextBlock(BasicBlock basicBlock) {
        if (basicBlock == null)
            return;
        nextBlocks.add(basicBlock);
        basicBlock.prevBlocks.add(this);
    }

    public void removeNextBlock(BasicBlock basicBlock) {
        if (basicBlock == null)
            return;
        nextBlocks.remove(basicBlock);
        basicBlock.prevBlocks.remove(this);
    }

    public void setEnd(BranchInst jumpInst) {
        append(jumpInst);
        end = true;
        if (jumpInst instanceof Branch) {
            addNextBlock(((Branch) jumpInst).getThenBlock());
            addNextBlock(((Branch) jumpInst).getElseBlock());
        } else if (jumpInst instanceof Jump) {
            addNextBlock(((Jump) jumpInst).getTargetBlock());
        } else if (jumpInst instanceof Return) {
            function.returnInstList.add((Return) jumpInst);
        } else {
            throw new CompileError("invalid jump inst");
        }
    }

    public void removeJumpInst() {
        end = false;
        if (tailInst instanceof  Branch) {
            removeNextBlock(((Branch) tailInst).getThenBlock());
            removeNextBlock(((Branch) tailInst).getElseBlock());
        } else if (tailInst instanceof Jump) {
            removeNextBlock(((Jump) tailInst).getTargetBlock());
        } else if (tailInst instanceof Return) {
            function.returnInstList.remove((Return) tailInst);
        } else {
            throw new CompileError("delete jump inst error");
        }
    }

    public void reset() {
        headInst = tailInst = null;
        end = false;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    //
    public IRFunction getFunction() {
        return function;
    }

    public void setFunction(IRFunction function) {
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IRInstruction getHeadInst() {
        return headInst;
    }

    public void setHeadInst(IRInstruction headInst) {
        this.headInst = headInst;
    }

    public IRInstruction getTailInst() {
        return tailInst;
    }

    public void setTailInst(IRInstruction tailInst) {
        this.tailInst = tailInst;
    }

    public Set<BasicBlock> getPrevBlocks() {
        return prevBlocks;
    }

    public void setPrevBlocks(Set<BasicBlock> prevBlocks) {
        this.prevBlocks = prevBlocks;
    }

    public Set<BasicBlock> getNextBlocks() {
        return nextBlocks;
    }

    public void setNextBlocks(Set<BasicBlock> nextBlocks) {
        this.nextBlocks = nextBlocks;
    }

    public boolean isEnd() {
        return end;
    }

    public ForStmtNode getForStmtNode() {
        return forStmtNode;
    }

    public void setForStmtNode(ForStmtNode forStmtNode) {
        this.forStmtNode = forStmtNode;
    }

    public static int getCnt() {
        return cnt;
    }

    public static void setCnt(int cnt) {
        BasicBlock.cnt = cnt;
    }

    public int getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }

    public int getPreOrderIndex() {
        return preOrderIndex;
    }

    public void setPreOrderIndex(int preOrderIndex) {
        this.preOrderIndex = preOrderIndex;
    }

    public int getPostOrderIndex() {
        return postOrderIndex;
    }

    public void setPostOrderIndex(int postOrderIndex) {
        this.postOrderIndex = postOrderIndex;
    }
}
