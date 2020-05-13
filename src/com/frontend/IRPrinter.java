package com.frontend;

import com.ir.BasicBlock;
import com.ir.IRFunction;
import com.ir.IRRoot;
import com.ir.IRVisitor;
import com.ir.instruction.*;
import com.ir.value.*;

import java.io.PrintStream;
import java.util.*;

public class IRPrinter implements IRVisitor {
    private PrintStream out;

    private Set<BasicBlock> visitedBlocks = new HashSet<>();

    private Map<BasicBlock, String> blockMap = new HashMap<>();
    private Map<String, Integer> blockCnt = new HashMap<>();

    private Map<StaticData, String> dataMap = new HashMap<>();
    private Map<String, Integer> dataCnt = new HashMap<>();

    private Map<VReg, String> regMap;
    private Map<String, Integer> regCnt;

    private boolean defineStatic = true;


    public IRPrinter(PrintStream out) {
        this.out = out;
    }

    private String makeId(String name, Map<String, Integer> counter) {
        int cnt = counter.getOrDefault(name, 0) + 1;
        counter.put(name, cnt);
        if (cnt == 1)
            return name;
        else
            return name + "_" + cnt;
    }

    private String getBlockId(BasicBlock basicBlock) {
        String id = blockMap.get(basicBlock);
        if (id == null) {
            if (basicBlock.getName() == null)
                id = makeId("block", blockCnt);
            else
                id = makeId(basicBlock.getName(), blockCnt);
            blockMap.put(basicBlock, id);
        }
        return id;
    }

    public String getDataId(StaticData staticData) {
        String id = dataMap.get(staticData);
        if (id == null) {
            if (staticData.getName() == null)
                id = makeId("staticData", dataCnt);
            else
                id = makeId(staticData.getName(), dataCnt);
            dataMap.put(staticData, id);
        }
        return id;
    }

    public String getRegId(VReg vReg) {
        String id = regMap.get(vReg);
        if (id == null) {
            if (vReg.getName() == null)
                id = makeId("vreg", regCnt);
            else
                id = makeId(vReg.getName(), regCnt);
            regMap.put(vReg, id);
        }
        return id;
    }

    @Override
    public void visit(BasicBlock node) {
        if (visitedBlocks.contains(node))
            return;
        visitedBlocks.add(node);
        out.println("%" + getBlockId(node) + ":");
        for (IRInstruction inst = node.getHeadInst(); inst != null; inst = inst.getNext()) {
            inst.accept(this);
        }
    }

    @Override
    public void visit(IRFunction node) {
        regMap = new IdentityHashMap<>();
        regCnt = new HashMap<>();
        out.printf("func %s ", node.getName());
        for (VReg vReg : node.getParamVRegList()) {
            out.printf("$%s ", getRegId(vReg));
        }
        out.print("{\n");
        for (BasicBlock basicBlock : node.getReversePostOrder()) {
            basicBlock.accept(this);
        }
        out.print("}\n\n");
    }

    @Override
    public void visit(IRRoot node) {
        defineStatic = true;
        for (StaticData staticData : node.getStaticDataList()) {
            staticData.accept(this);
        }
        for (StaticStr staticStr : node.getStaticStrMap().values()) {
            staticStr.accept(this);
        }
        out.println();

        defineStatic = false;
        for (IRFunction function : node.getFunctionMap().values()) {
            function.accept(this);
        }
    }

    @Override
    public void visit(BinaryOperation node) {
        out.print("    ");
        String bop;
        switch (node.getBop()) {
            case Add:
                bop = "add";
                break;
            case Div:
                bop = "div";
                break;
            case Mod:
                bop = "rem";
                break;
            case Mul:
                bop = "mul";
                break;
            case Sub:
                bop = "sub";
                break;
            case BitwiseOr:
                bop = "or";
                break;
            case ShiftLeft:
                bop = "shl";
                break;
            case BitwiseAnd:
                bop = "and";
                break;
            case BitwiseXor:
                bop = "xor";
                break;
            case ShiftRight:
                bop = "shr";
                break;
            default:
                bop = "error";
        }
        node.getDestReg().accept(this);
        out.printf(" = %s ", bop);
        node.getLhs().accept(this);
        out.print(" ");
        node.getRhs().accept(this);
        out.println();
    }

    @Override
    public void visit(UnaryOperation node) {
        out.print("    ");
        String op;
        switch (node.getOp()) {
            case Neg:
                op = "neg";
                break;
            case BitwiseNot:
                op = "not";
                break;
            default:
                op = "error";
        }
        node.getDestReg().accept(this);
        out.printf(" = %s ", op);
        node.getOperand().accept(this);
        out.println();
    }

    @Override
    public void visit(Comparison node) {
        out.print("    ");
        String cmp;
        switch (node.getCmp()) {
            case Geq:
                cmp = "sge";
                break;
            case Leq:
                cmp = "sle";
                break;
            case Neq:
                cmp = "sne";
                break;
            case Less:
                cmp = "slt";
                break;
            case Equal:
                cmp = "seq";
                break;
            case Greater:
                cmp = "sgt";
                break;
            default:
                cmp = "error";
        }
        node.getDestReg().accept(this);
        out.printf(" = %s ", cmp);
        node.getLhs().accept(this);
        out.print(" ");
        node.getRhs().accept(this);
        out.println();
    }

    @Override
    public void visit(Immediate node) {
        out.print(node.getValue());
    }

    @Override
    public void visit(FuncCall node) {
        out.print("    ");
        if (node.getRetReg() != null) {
            node.getRetReg().accept(this);
            out.print(" = ");
        }
        out.printf("call %s ", node.getFunction().getName());
        for (IntValue param : node.getParams()) {
            param.accept(this);
            out.print(" ");
        }
        out.println();
    }

    @Override
    public void visit(Branch node) {
        out.print("    br ");
        node.getCond().accept(this);
        out.println(" %" + getBlockId(node.getThenBlock()) + " %" + getBlockId(node.getElseBlock()));
        out.println();
    }

    @Override
    public void visit(Return node) {
        out.print("    ret ");
        if (node.getRetValue() != null) {
            node.getRetValue().accept(this);
        }
        out.println();
        out.println();
    }

    @Override
    public void visit(Jump node) {
        out.printf("    jump %%%s\n\n", getBlockId(node.getTargetBlock()));
    }

    @Override
    public void visit(HeapAllocate node) {
        out.print("    ");
        node.getDestReg().accept(this);
        out.print(" = alloc ");
        node.getAllocSize().accept(this);
        out.println();
    }

    @Override
    public void visit(Load node) {
        out.print("    ");
        node.getDestReg().accept(this);
        out.printf(" = load %d ", node.getSize());
        node.getAddr().accept(this);
        out.println(" " + node.getOffset());
    }

    @Override
    public void visit(Store node) {
        out.printf("    store %d ", node.getSize());
        node.getAddr().accept(this);
        out.print(" ");
        node.getValue().accept(this);
        out.println(" " + node.getOffset());
    }

    @Override
    public void visit(Move node) {
        out.print("    ");
        node.getDestReg().accept(this);
        out.print(" = move ");
        node.getSrc().accept(this);
        out.println();
    }

    @Override
    public void visit(StackSlot node) {
    }

    @Override
    public void visit(PReg node) {
    }

    @Override
    public void visit(VReg node) {
        out.print("$" + getRegId(node));
    }

    @Override
    public void visit(StaticStr node) {
        if (defineStatic)
            out.printf("str @%s %s\n", getDataId(node), node.getValue());
        else
            out.print("@" + getDataId(node));
    }

    @Override
    public void visit(StaticVar node) {
        if (defineStatic)
            out.printf("var @%s %d\n", getDataId(node), node.getSize());
        else
            out.print("@" + getDataId(node));
    }
}
