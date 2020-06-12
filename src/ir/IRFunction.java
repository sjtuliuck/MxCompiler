package ir;

import frontend.IRBuilder;
import frontend.entity.FuncEntity;
import ir.instruction.FuncCall;
import ir.instruction.IRInstruction;
import ir.instruction.Return;
import ir.value.PReg;
import ir.value.StackSlot;
import ir.value.VReg;

import java.util.*;

public class IRFunction {
    private String name;
    private BasicBlock entryBlock;
    private BasicBlock exitBlock;
    private FuncEntity funcEntity;

    private boolean buildIn = false;
    private String buildInName;

    private boolean inClass = false;

    public List<VReg> paramVRegList = new ArrayList<>();

    // cfg info
    private List<BasicBlock> reversePreOrder = null;
    private List<BasicBlock> reversePostOrder = null;
    private Set<BasicBlock> visitedBlock = null;
    public List<Return> returnInstList = new ArrayList<>();
    public Set<IRFunction> calleeSet = new HashSet<>();
    public Set<IRFunction> recursiveCalleeSet = new HashSet<>();

    // register allocation info
    public Map<VReg, StackSlot> paramStackMap = new HashMap<>();
    public List<StackSlot> stackSlotList = new ArrayList<>();
    public Set<PReg> usedGeneralPReg = new HashSet<>();

    public IRFunction() {
    }

    public IRFunction(FuncEntity funcEntity) {
        this.name = funcEntity.getIdentifier();
        this.funcEntity = funcEntity;
        if (funcEntity.isInClass()) {
            this.name = IRBuilder.makeClassFuncName(funcEntity.getClassIdentifier(), name);
            this.inClass = true;
        }
    }

    // build-in function
    public IRFunction(String name) {
        this.name = name;
        this.buildIn = true;
        this.funcEntity = null;
    }


    public void updateCalleeSet() {
        calleeSet.clear();
        for (BasicBlock basicBlock : getReversePostOrder()) {
            IRInstruction inst = null;
            for (inst = basicBlock.getHeadInst(); inst != null; inst = inst.getNext()) {
                if (inst instanceof FuncCall) {
                    calleeSet.add(((FuncCall) inst).getFunction());
                }
            }
        }
    }

    public void genEntryBlock() {
        entryBlock = new BasicBlock(this, "__entry__" + name);
    }

    private void dfsPostOrder(BasicBlock basicBlock) {
        if (visitedBlock.contains(basicBlock))
            return;
        visitedBlock.add(basicBlock);
        for (BasicBlock block : basicBlock.getNextBlocks()) {
            dfsPostOrder(block);
        }
        reversePostOrder.add(basicBlock);
    }

    public void calcReversePostOrder() {
        visitedBlock = new HashSet<>();
        reversePostOrder = new ArrayList<>();
        dfsPostOrder(entryBlock);
        visitedBlock = null;
        for (int i = 0; i < reversePostOrder.size(); ++i) {
            reversePostOrder.get(i).setPostOrderIndex(i);
        }
        Collections.reverse(reversePostOrder);
    }

    private void dfsPreOrder(BasicBlock basicBlock) {
        if (visitedBlock.contains(basicBlock))
            return;
        visitedBlock.add(basicBlock);
        reversePreOrder.add(basicBlock);
        for (BasicBlock block : basicBlock.getNextBlocks()) {
            dfsPreOrder(block);
        }
    }

    public void calcReversePreOrder() {
        visitedBlock = new HashSet<>();
        reversePreOrder = new ArrayList<>();
        dfsPreOrder(entryBlock);
        visitedBlock = null;
        for (int i = 0; i < reversePreOrder.size(); ++i) {
            reversePreOrder.get(i).setPreOrderIndex(i);
        }
        Collections.reverse(reversePreOrder);
    }

    public void refreshCFG(BasicBlock newEntryBlock, BasicBlock newExitBlock) {
        reversePreOrder = null;
        reversePostOrder = null;
        entryBlock = newEntryBlock;
        exitBlock = newExitBlock;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public void addParamReg(VReg vReg) {
        paramVRegList.add(vReg);
    }

    //
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }

    public void setEntryBlock(BasicBlock entryBlock) {
        this.entryBlock = entryBlock;
    }

    public BasicBlock getExitBlock() {
        return exitBlock;
    }

    public void setExitBlock(BasicBlock exitBlock) {
        this.exitBlock = exitBlock;
    }

    public FuncEntity getFuncEntity() {
        return funcEntity;
    }

    public void setFuncEntity(FuncEntity funcEntity) {
        this.funcEntity = funcEntity;
    }

    public boolean isBuildIn() {
        return buildIn;
    }

    public void setBuildIn(boolean buildIn) {
        this.buildIn = buildIn;
    }

    public String getBuildInName() {
        return buildInName;
    }

    public void setBuildInName(String buildInName) {
        this.buildInName = buildInName;
    }

    public boolean isInClass() {
        return inClass;
    }

    public void setInClass(boolean inClass) {
        this.inClass = inClass;
    }

    public List<BasicBlock> getReversePreOrder() {
        if (reversePostOrder == null)
            calcReversePostOrder();
        return reversePreOrder;
    }

    public void setReversePreOrder(List<BasicBlock> reversePreOrder) {
        this.reversePreOrder = reversePreOrder;
    }

    public List<BasicBlock> getReversePostOrder() {
        if (reversePreOrder == null)
            calcReversePreOrder();
        return reversePostOrder;
    }

    public void setReversePostOrder(List<BasicBlock> reversePostOrder) {
        this.reversePostOrder = reversePostOrder;
    }

    public Set<BasicBlock> getVisitedBlock() {
        return visitedBlock;
    }

    public void setVisitedBlock(Set<BasicBlock> visitedBlock) {
        this.visitedBlock = visitedBlock;
    }

    public List<VReg> getParamVRegList() {
        return paramVRegList;
    }

    public void setParamVRegList(List<VReg> paramVRegList) {
        this.paramVRegList = paramVRegList;
    }

    public List<Return> getReturnInstList() {
        return returnInstList;
    }

    public void setReturnInstList(List<Return> returnInstList) {
        this.returnInstList = returnInstList;
    }

    public Set<IRFunction> getCalleeSet() {
        return calleeSet;
    }

    public void setCalleeSet(Set<IRFunction> calleeSet) {
        this.calleeSet = calleeSet;
    }

    public Set<IRFunction> getRecursiveCalleeSet() {
        return recursiveCalleeSet;
    }

    public void setRecursiveCalleeSet(Set<IRFunction> recursiveCalleeSet) {
        this.recursiveCalleeSet = recursiveCalleeSet;
    }

    public Map<VReg, StackSlot> getParamStackMap() {
        return paramStackMap;
    }

    public void setParamStackMap(Map<VReg, StackSlot> paramStackMap) {
        this.paramStackMap = paramStackMap;
    }

    public List<StackSlot> getStackSlotList() {
        return stackSlotList;
    }

    public void setStackSlotList(List<StackSlot> stackSlotList) {
        this.stackSlotList = stackSlotList;
    }

    public Set<PReg> getUsedGeneralPReg() {
        return usedGeneralPReg;
    }

    public void setUsedGeneralPReg(Set<PReg> usedGeneralPReg) {
        this.usedGeneralPReg = usedGeneralPReg;
    }
}
