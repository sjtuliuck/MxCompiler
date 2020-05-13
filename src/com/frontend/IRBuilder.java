package com.frontend;

import com.ast.*;
import com.frontend.entity.ClassEntity;
import com.frontend.entity.FuncEntity;
import com.frontend.entity.VarEntity;
import com.frontend.type.ClassType;
import com.frontend.type.Type;
import com.frontend.type.VoidType;
import com.ir.BasicBlock;
import com.ir.GlobalVarInit;
import com.ir.IRFunction;
import com.ir.IRRoot;
import com.ir.instruction.*;
import com.ir.value.*;
import com.utility.CompileError;
import com.utility.Tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IRBuilder implements ASTVisitor {
    private Scope globalScope;
    private Scope curScope;

    private IRRoot irRoot;
    private List<GlobalVarInit> globalVarInitList = new ArrayList<>();

    private BasicBlock curBlock;
    private String curClass;
    private IRFunction curFunc;
    // loop continue, break
    private BasicBlock curLoopStepBlock, curLoopAfterBlock;
    private boolean memAccess = false;
    private boolean isFuncArg = false;

    public IRBuilder(Scope globalScope) {
        this.globalScope = globalScope;
        irRoot = new IRRoot();
    }

    public static String makeClassFuncName(String className, String funcName) {
        return "__class__" + className + "__" + funcName;
    }

    private FuncDefNode globalVarInitFunc() {
        String globalVarInitName = "__globalVarInit";
        List<StmtNode> varInitStmtList = new ArrayList<>();
        for (GlobalVarInit globalVarInit : globalVarInitList) {
            IdentifierExprNode lhs = new IdentifierExprNode(null, globalVarInit.getName());
            VarEntity varEntity = globalScope.getVar(globalVarInit.getName());
            lhs.setVarEntity(varEntity);
            varInitStmtList.add(new ExprStmtNode(null, new BinaryExprNode(null, BinaryExprNode.BinOp.assign, lhs, globalVarInit.getInitValue())));
        }
        BlockStmtNode globalInitBlock = new BlockStmtNode(null, varInitStmtList);
        globalInitBlock.setScope(new Scope(globalScope));
        FuncDefNode globalVarInitFunc = new FuncDefNode(null, new TypeNode(null, new VoidType()), globalVarInitName, new ArrayList<>(), globalInitBlock);
        FuncEntity funcEntity = new FuncEntity(globalVarInitFunc, globalInitBlock.getScope());
        globalScope.addFunc(funcEntity);
        IRFunction globalInitIrFunc = new IRFunction(funcEntity);
        irRoot.addFunction(globalInitIrFunc);
        return globalVarInitFunc;
    }


    @Override
    public void visit(ProgramNode node) {
        curScope = globalScope;
        for (DefNode defNode : node.getDefNodeList()) {
            if (defNode instanceof VarDefNode) { //fixme
                defNode.accept(this);
            } else if (defNode instanceof FuncDefNode) {
                FuncEntity funcEntity = curScope.getFunc(((FuncDefNode) defNode).getIdentifier());
                irRoot.addFunction(new IRFunction(funcEntity));
            } else if (defNode instanceof ClassDefNode) {
                ClassEntity classEntity = curScope.getClass(((ClassDefNode) defNode).getIdentifier());
                curScope = classEntity.getClassScope();
                for (FuncDefNode funcDefNode : ((ClassDefNode) defNode).getFuncDefNodeList()) {
                    FuncEntity funcEntity = curScope.getFunc(funcDefNode.getIdentifier());
                    irRoot.addFunction(new IRFunction(funcEntity));
                }
                curScope = curScope.getFather();
            }
        }

        FuncDefNode varInitFunc = globalVarInitFunc();
        varInitFunc.accept(this);

        for (DefNode defNode : node.getDefNodeList()) {
            if (defNode instanceof FuncDefNode) {
                defNode.accept(this);
            } else if (defNode instanceof ClassDefNode) {
                defNode.accept(this);
            } else if (!(defNode instanceof VarDefNode)) {
                throw new CompileError("invalid defNode");
            }
        }
        calcRecursiveCalleeSet();
    }

    @Override
    public void visit(VarDefNode node) {
        if (node.getVarNodeList() != null) {
            for (VarNode varNode : node.getVarNodeList()) {
                varNode.accept(this);
            }
        }
    }

    @Override
    public void visit(ClassDefNode node) {
        curClass = node.getIdentifier();
        curScope = globalScope;
        for (FuncDefNode funcDefNode : node.getFuncDefNodeList()) {
            funcDefNode.accept(this);
        }
        curClass = null;
    }

    @Override
    public void visit(FuncDefNode node) {
        String identifier = node.getIdentifier();
        if (curClass != null)
            identifier = makeClassFuncName(curClass, identifier);
        curFunc = irRoot.getFunctionMap().get(identifier);
        curFunc.genEntryBlock();
        curBlock = curFunc.getEntryBlock();

        Scope oldScope = curScope;
        BlockStmtNode blockStmtNode = (BlockStmtNode) node.getStmt();
        curScope = blockStmtNode.getScope();
        if (curClass != null) {
            VReg vReg = new VReg("this");
            curScope.getVar("this").setIrReg(vReg);
            curFunc.addParamReg(vReg);
        }
        isFuncArg = true;
        for (VarNode varNode : node.getParamList()) {
            varNode.accept(this);
        }
        isFuncArg = false;
        curScope = oldScope;
        // main
        if (node.getIdentifier().equals("main")) {
            curBlock.append(new FuncCall(curBlock, null, irRoot.getFunctionMap().get("__globalVarInit"), new ArrayList<>()));
        }
        blockStmtNode.accept(this);
        // default return value
        if (!curBlock.isEnd()) {
            if (node.getRetType() == null || node.getRetType().getType().getTypeName().equals("void"))
                curBlock.setEnd(new Return(curBlock, null));
            else
                curBlock.setEnd(new Return(curBlock, new Immediate(0)));
        }

        // more than 1 return
        if (curFunc.getReturnInstList().size() > 1) {
            BasicBlock mergeRetBlock = new BasicBlock(curFunc, "__mergeReturn__" + curFunc.getName());
            VReg returnReg;
            if (node.getRetType() == null || node.getRetType().getType().getTypeName().equals("null"))
                returnReg = null;
            else
                returnReg = new VReg("returnReg");

            List<Return> returnList = new ArrayList<>(curFunc.getReturnInstList());
            for (Return returnInst : returnList) {
                BasicBlock basicBlock = returnInst.getCurBlock();
                if (returnInst.getRetValue() != null)
                    returnInst.insertPrev(new Move(basicBlock, returnReg, returnInst.getRetValue()));
                returnInst.remove();
                basicBlock.setEnd(new Jump(curBlock, mergeRetBlock));
            }
            mergeRetBlock.setEnd(new Return(mergeRetBlock, returnReg));
            curFunc.setExitBlock(mergeRetBlock);
        } else {
            curFunc.setExitBlock(curFunc.getReturnInstList().get(0).getCurBlock());
        }
        curFunc = null;
    }

    @Override
    public void visit(VarDefStmtNode node) {
        if (node.getVarNodeList() != null) {
            for (VarNode varNode : node.getVarNodeList()) {
                varNode.accept(this);
            }
        }
    }

    @Override
    public void visit(ExprStmtNode node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(IfStmtNode node) {
        BasicBlock thenBlock = new BasicBlock(curFunc, "if_then");
        BasicBlock elseBlock = null;
        BasicBlock afterBlock = new BasicBlock(curFunc, "if_after");

        node.getCondExpr().setTrueBlock(thenBlock);

        if (node.getElseStmt() != null) {
            elseBlock = new BasicBlock(curFunc, "if_else");
            node.getCondExpr().setFalseBlock(elseBlock);
        } else {
            node.getCondExpr().setFalseBlock(afterBlock);
        }

        node.getCondExpr().accept(this);
        if (node.getCondExpr().getType().getTypeName().equals("bool")) {
            curBlock.setEnd(new Branch(curBlock, node.getCondExpr().getReg(), node.getCondExpr().getTrueBlock(), node.getCondExpr().getFalseBlock()));
        }

        curBlock = thenBlock;
        node.getThenStmt().accept(this);
        if (!curBlock.isEnd())
            curBlock.setEnd(new Jump(curBlock, afterBlock));
        if (node.getElseStmt() != null) {
            curBlock = elseBlock;
            node.getElseStmt().accept(this);
            if (!curBlock.isEnd())
                curBlock.setEnd(new Jump(curBlock, afterBlock));
        }
        curBlock = afterBlock;
    }

    @Override
    public void visit(WhileStmtNode node) {
        BasicBlock condBlock = new BasicBlock(curFunc, "while_cond");
        BasicBlock bodyBlock = new BasicBlock(curFunc, "while_body");
        BasicBlock afterBlock = new BasicBlock(curFunc, "while_after");

        BasicBlock oldLoopCondBlock = curLoopStepBlock;
        BasicBlock oldLoopAfterBlock = curLoopAfterBlock;
        curLoopStepBlock = condBlock;
        curLoopAfterBlock = afterBlock;

        curBlock.setEnd(new Jump(curBlock, condBlock));

        curBlock = condBlock;
        node.getCondExpr().setTrueBlock(bodyBlock);
        node.getCondExpr().setFalseBlock(afterBlock);
        node.getCondExpr().accept(this);

        if (node.getCondExpr().getType().getTypeName().equals("bool")) {
            curBlock.setEnd(new Branch(curBlock, node.getCondExpr().getReg(), node.getCondExpr().getTrueBlock(), node.getCondExpr().getFalseBlock()));
        }

        curBlock = bodyBlock;
        node.getBodyStmt().accept(this);
        if (!curBlock.isEnd()) {
            curBlock.setEnd(new Jump(curBlock, condBlock));
        }

        curLoopStepBlock = oldLoopCondBlock;
        curLoopAfterBlock = oldLoopAfterBlock;
        curBlock = afterBlock;
    }

    @Override
    public void visit(ForStmtNode node) {
        BasicBlock condBlock;
        BasicBlock stepBlock;
        BasicBlock bodyBlock = new BasicBlock(curFunc, "for_body");
        BasicBlock afterBlock = new BasicBlock(curFunc, "for_after");

        if (node.getCondExpr() != null) {
            condBlock = new BasicBlock(curFunc, "for_cond");
        } else {
            condBlock = bodyBlock;
        }
        if (node.getStepExpr() != null) {
            stepBlock = new BasicBlock(curFunc, "for_step");
        } else {
            stepBlock = condBlock;
        }
        // fixme forstmtnode
        //
        BasicBlock oldStepBlock = curLoopStepBlock;
        BasicBlock oldAfterBlock = curLoopAfterBlock;
        curLoopStepBlock = stepBlock;
        curLoopAfterBlock = afterBlock;

        // init
        if (node.getInitExpr() != null)
            node.getInitExpr().accept(this);
        curBlock.setEnd(new Jump(curBlock, condBlock));

        // cond
        if (node.getCondExpr() != null) {
            curBlock = condBlock;
            node.getCondExpr().setTrueBlock(bodyBlock);
            node.getCondExpr().setFalseBlock(afterBlock);
            node.getCondExpr().accept(this);
            if (node.getCondExpr() instanceof BoolConstNode) {
                curBlock.setEnd(new Branch(curBlock, node.getCondExpr().getReg(), node.getCondExpr().getTrueBlock(), node.getCondExpr().getFalseBlock()));
            }
        }

        // step
        if (node.getStepExpr() != null) {
            curBlock = stepBlock;
            node.getStepExpr().accept(this);
            curBlock.setEnd(new Jump(curBlock, condBlock));
        }

        // body
        curBlock = bodyBlock;
        if (node.getBodyStmt() != null) {
            node.getBodyStmt().accept(this);
        }
        if (!curBlock.isEnd())
            curBlock.setEnd(new Jump(curBlock, stepBlock));

        //
        curBlock = afterBlock;
        curLoopStepBlock = oldStepBlock;
        curLoopAfterBlock = oldAfterBlock;
    }

    @Override
    public void visit(ReturnStmtNode node) {
        Type retType = curFunc.getFuncEntity().getRetType();
        if (retType == null || retType.getTypeName().equals("void")) {
            curBlock.setEnd(new Return(curBlock, null));
        } else if (!(node.getRetExpr() instanceof BoolConstNode) && retType.getTypeName().equals("bool")) {
            VReg vReg = new VReg("boolReturnReg");
            node.getRetExpr().setTrueBlock(new BasicBlock(curFunc, null));
            node.getRetExpr().setFalseBlock(new BasicBlock(curFunc, null));
            node.getRetExpr().accept(this);
            processAssign(vReg, node.getRetExpr(), 0, Tools.regSize, false);
            curBlock.setEnd(new Return(curBlock, vReg));
        }  else {
            node.getRetExpr().accept(this);
            curBlock.setEnd(new Return(curBlock, node.getRetExpr().getReg()));
        }
    }

    @Override
    public void visit(BreakStmtNode node) {
        curBlock.setEnd(new Jump(curBlock, curLoopAfterBlock));
    }

    @Override
    public void visit(ContinueStmtNode node) {
        curBlock.setEnd(new Jump(curBlock, curLoopStepBlock));
    }

    @Override
    public void visit(BlockStmtNode node) {
        curScope = node.getScope();
        for (StmtNode stmtNode : node.getStmtNodeList()) {
            stmtNode.accept(this);
            if (curBlock.isEnd())
                break;
        }
        curScope = curScope.getFather();
    }

    @Override
    public void visit(ExprListNode node) {
    }

    @Override
    public void visit(IdentifierExprNode node) {
        VarEntity varEntity = node.getVarEntity();
        if (varEntity.getIrReg() != null) {
            node.setReg(varEntity.getIrReg());
            if (node.getFalseBlock() != null)
                curBlock.setEnd(new Branch(curBlock, varEntity.getIrReg(), node.getTrueBlock(), node.getFalseBlock()));
        } else {
            ThisExprNode thisExprNode = new ThisExprNode(null);
            thisExprNode.setType(new ClassType(curClass));
            MemberExprNode memberExprNode = new MemberExprNode(null, thisExprNode, node.getIdentifier());
            memberExprNode.accept(this);
            if (memAccess) {
                node.setAddr(memberExprNode.getAddr());
                node.setOffset(memberExprNode.getOffset());
            } else {
                node.setReg(memberExprNode.getReg());
                if (node.getFalseBlock() != null)
                    curBlock.setEnd(new Branch(curBlock, memberExprNode.getReg(), node.getTrueBlock(), node.getFalseBlock()));
            }
            node.setMemAccessing(true);
        }
    }

    @Override
    public void visit(ThisExprNode node) {
        VarEntity varEntity = curScope.getVar("this");
        node.setReg(varEntity.getIrReg());
        if (node.getFalseBlock() != null) {
            curBlock.setEnd(new Branch(curBlock, varEntity.getIrReg(), node.getTrueBlock(), node.getFalseBlock()));
        }
    }

    @Override
    public void visit(MemberExprNode node) {
        boolean oldMemAccess = memAccess;
        memAccess = false;
        node.getExpr().accept(this);
        memAccess = oldMemAccess;

        String identifier = ((ClassType) node.getExpr().getType()).getIdentifier();
        IntValue classAddr = node.getExpr().getReg();
        ClassEntity classEntity = curScope.getClass(identifier);
        VarEntity varEntity = classEntity.getClassScope().getLocalVar(node.getIdentifier());

        if (memAccess) {
            node.setAddr(classAddr);
            node.setOffset(varEntity.getMemOffset());
        } else {
            VReg destReg = new VReg(null);
            node.setReg(destReg);
            curBlock.append(new Load(curBlock, destReg, classAddr, varEntity.getType().getRegSize(), varEntity.getMemOffset()));
            if (node.getFalseBlock() != null)
                curBlock.setEnd(new Branch(curBlock, destReg, node.getTrueBlock(), node.getFalseBlock()));
        }
    }

    @Override
    public void visit(ArrayExprNode node) {
        boolean oldMemAccess = memAccess;
        memAccess = false;
        node.getArray().accept(this);
        node.getIdx().accept(this);
        memAccess = oldMemAccess;

        VReg destReg = new VReg(null);
        Immediate elemSize = new Immediate(node.getArray().getType().getRegSize());
        curBlock.append(new BinaryOperation(curBlock, destReg, BinaryOperation.BinaryOp.Mul, node.getIdx().getReg(), elemSize));
        curBlock.append(new BinaryOperation(curBlock, destReg, BinaryOperation.BinaryOp.Add, node.getArray().getReg(), destReg));
        if (memAccess) {
            node.setAddr(destReg);
            node.setOffset(Tools.regSize);
        } else {
            node.setReg(destReg);
            curBlock.append(new Load(curBlock, destReg, destReg, node.getArray().getType().getRegSize(), Tools.regSize));
            if (node.getFalseBlock() != null) {
                curBlock.setEnd(new Branch(curBlock, destReg, node.getTrueBlock(), node.getFalseBlock()));
            }
        }
    }

    @Override
    public void visit(FuncExprNode node) {
        FuncEntity funcEntity = node.getFuncEntity();
        String funcName = funcEntity.getIdentifier();
        ExprNode thisExpr = null;
        List<IntValue> paramList = new ArrayList<>();

        if (funcEntity.isInClass()) {
            if (node.getExpr() instanceof MemberExprNode) {
                thisExpr = ((MemberExprNode) node.getExpr()).getExpr();
            } else {
                if (curClass != null) {
                    thisExpr = new ThisExprNode(null);
                    thisExpr.setType(new ClassType(curClass));
                } else {
                    throw new CompileError("invalid func");
                }
            }
            thisExpr.accept(this);
            String className;
            switch (thisExpr.getType().getTypeName()) {
                case "class":
                    className = ((ClassType) thisExpr.getType()).getIdentifier();
                    break;
                case "array":
                    className = "#array#";
                    break;
                case "string":
                    className = "#string#";
                    break;
                default:
                    throw new CompileError("invalid class");
            }

            funcName = makeClassFuncName(className, funcName);
            paramList.add(thisExpr.getReg());
        }
        // build in
        if (funcEntity.isBuildInFunc()) {
            processBuildInFunc(node, funcName, funcEntity, thisExpr);
        } else {
            for (ExprNode param : node.getParamList()) {
                param.accept(this);
                paramList.add(param.getReg());
            }
            VReg destReg = new VReg(null);
            IRFunction calleeFunc = irRoot.getFunctionMap().get(funcName);
            curBlock.append(new FuncCall(curBlock, destReg, calleeFunc, paramList));
            node.setReg(destReg);
            if (node.getFalseBlock() != null) {
                curBlock.setEnd(new Branch(curBlock, destReg, node.getTrueBlock(), node.getFalseBlock()));
            }
        }
    }

    @Override
    public void visit(NewExprNode node) {
        Type newType = node.getNewType();
        VReg destReg = new VReg(null);
        if (newType.getTypeName().equals("array")) {
            processNewArray(node, null, destReg, 0);
        } else if (newType.getTypeName().equals("class")) {
            String identifier = ((ClassType) newType).getIdentifier();
            ClassEntity classEntity = globalScope.getClass(identifier);
            curBlock.append(new HeapAllocate(curBlock, destReg, new Immediate(classEntity.getMemSize())));
            String constructFuncName = makeClassFuncName(identifier, identifier);
            IRFunction constructFunc = irRoot.getFunctionMap().get(constructFuncName);
            if (constructFunc != null) {
                List<IntValue> paramList = new ArrayList<>();
                paramList.add(destReg);
                curBlock.append(new FuncCall(curBlock, null, constructFunc, paramList));
            }
        } else {
            throw new CompileError("invalid new type");
        }
        node.setReg(destReg);
    }

    @Override
    public void visit(PostfixExprNode node) {
        switch (node.getOp()) {
            case postInc:
                processSelfIncDec(node, "++");
                break;
            case postDec:
                processSelfIncDec(node, "--");
                break;
        }
    }

    @Override
    public void visit(PrefixExprNode node) {
        VReg destReg;
        switch (node.getOp()) {
            case preInc:
                processSelfIncDec(node, "++");
                break;
            case preDec:
                processSelfIncDec(node, "--");
                break;
            case signPos:
                node.setReg(node.getExpr().getReg());
                break;
            case signNeg:
                destReg = new VReg(null);
                node.setReg(destReg);
                node.getExpr().accept(this);
                curBlock.append(new UnaryOperation(curBlock, destReg, UnaryOperation.UnaryOp.Neg, node.getExpr().getReg()));
                break;
            case bitwiseNot:
                destReg = new VReg(null);
                node.setReg(destReg);
                node.getExpr().accept(this);
                curBlock.append(new UnaryOperation(curBlock, destReg, UnaryOperation.UnaryOp.BitwiseNot, node.getExpr().getReg()));
                break;
            case logicNot:
                node.getExpr().setTrueBlock(node.getTrueBlock());
                node.getExpr().setFalseBlock(node.getFalseBlock());
                node.getExpr().accept(this);
                break;
        }
    }

    @Override
    public void visit(BinaryExprNode node) {
        switch (node.getOp()) {
            case mul:
            case div:
            case mod:
            case add:
            case sub:
            case shiftLeft:
            case shiftRight:
            case bitwiseAnd:
            case bitwiseOr:
            case bitwiseXor:
                processBinaryArithString(node);
                break;
            case equal:
            case neq:
            case less:
            case greater:
            case leq:
            case geq:
                processBinaryCompareString(node);
                break;
            case logicAnd:
            case logicOr:
                processBinaryLogic(node);
                break;
            case assign:
                processAssign(node);
        }
    }

    @Override
    public void visit(BoolConstNode node) {
        if (node.getValue())
            node.setReg(new Immediate(1));
        else
            node.setReg(new Immediate(0));
    }

    @Override
    public void visit(IntConstNode node) {
        node.setReg(new Immediate(node.getValue()));
    }

    @Override
    public void visit(StringConstNode node) {
        StaticStr staticStr = irRoot.getStaticStrMap().get(node.getValue());
        if (staticStr == null) {
            // fixme
//            staticStr = new StaticStr(node.getValue());

        }
        node.setReg(staticStr);
    }

    @Override
    public void visit(NullConstNode node) {
        node.setReg(new Immediate(0));
    }

    @Override
    public void visit(TypeNode node) {
    }

    @Override
    public void visit(VarNode node) {
        VarEntity varEntity = curScope.getVar(node.getIdentifier());
        if (varEntity.isUnUsed())
            return;
        if (curScope.getFather() != null) {
            VReg vReg = new VReg(varEntity.getIdentifier());
            varEntity.setIrReg(vReg);
            if (isFuncArg)
                curFunc.addParamReg(vReg);
            if (node.getInitExpr() == null) {
                if (!isFuncArg)
                    curBlock.append(new Move(curBlock, vReg, new Immediate(0)));
            } else {
                if (!(node.getInitExpr() instanceof BoolConstNode && node.getInitExpr().getType().getTypeName().equals("bool"))) {
                    node.getInitExpr().setTrueBlock(new BasicBlock(curFunc, "trueBlock"));
                    node.getInitExpr().setFalseBlock(new BasicBlock(curFunc, "falseBlock"));
                }
                node.getInitExpr().accept(this);
                processAssign(vReg, node.getInitExpr(), 0, Tools.regSize, false);
            }
        } else {
            StaticVar staticVar = new StaticVar(node.getIdentifier(), Tools.regSize);
            irRoot.addStaticData(staticVar);
            varEntity.setIrReg(staticVar);
            if (node.getInitExpr() != null) {
                GlobalVarInit globalVarInit = new GlobalVarInit(node.getIdentifier(), node.getInitExpr());
                globalVarInitList.add(globalVarInit);
            }
        }
    }

    @Override
    public void visit(VarListNode node) {
    }

    private void calcRecursiveCalleeSet() {
        Set<IRFunction> recursiveCalleeSet = new HashSet<>();
        boolean changed = true;
        for (IRFunction irFunction : irRoot.getFunctionMap().values()) {
            irFunction.updateCalleeSet();
            irFunction.recursiveCalleeSet.clear();
        }
        while (changed) {
            changed = false;
            for (IRFunction irFunction : irRoot.getFunctionMap().values()) {
                recursiveCalleeSet.clear();
                recursiveCalleeSet.addAll(irFunction.calleeSet);
                for (IRFunction calleeFunc : irFunction.calleeSet) {
                    recursiveCalleeSet.addAll(calleeFunc.recursiveCalleeSet);
                }
                if (!(irFunction.recursiveCalleeSet.equals(recursiveCalleeSet))) {
                    irFunction.recursiveCalleeSet.clear();
                    irFunction.recursiveCalleeSet.addAll(recursiveCalleeSet);
                    changed = true;
                }
            }
        }
    }

    private void processorBinaryString(BinaryExprNode node) {
        if (node.getLhs().getType().getTypeName().equals("string")) {
            node.getLhs().accept(this);
            node.getRhs().accept(this);
            ExprNode tmp = null;
            IRFunction stringProcessFunc = null;
            switch (node.getOp()) {
                case add:
                    stringProcessFunc = irRoot.getBuildInFuncMap().get("__string__link");
                    break;
                case less:
                    stringProcessFunc = irRoot.getBuildInFuncMap().get("__string__less");
                    break;
                case leq:
                    stringProcessFunc = irRoot.getBuildInFuncMap().get("__string__leq");
                case greater:
                    stringProcessFunc = irRoot.getBuildInFuncMap().get("__string__greater");
                    break;
                case geq:
                    stringProcessFunc = irRoot.getBuildInFuncMap().get("__string__geq");
                    break;
                case equal:
                    stringProcessFunc = irRoot.getBuildInFuncMap().get("__string__equal");
                    break;
                case neq:
                    stringProcessFunc = irRoot.getBuildInFuncMap().get("__string__neq");
                    break;
                default:
                    throw new CompileError("invalid binary string op");
            }

            VReg destReg = new VReg(null);
            List<IntValue> funcParams = new ArrayList<>();
            funcParams.add(node.getLhs().getReg());
            funcParams.add(node.getRhs().getReg());
            curBlock.append(new FuncCall(curBlock, destReg, stringProcessFunc, funcParams));

            if (node.getFalseBlock() != null)
                curBlock.setEnd(new Branch(curBlock, destReg, node.getTrueBlock(), node.getFalseBlock()));
            else
                node.setReg(destReg);
        } else {
            throw new CompileError("string invalid type");
        }
    }

    private void processSelfIncDec(ExprNode node, )

    private void processBinaryCompare(BinaryExprNode node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        IntValue lhsValue = node.getLhs().getReg();
        IntValue rhsValue = node.getRhs().getReg();
        IntValue tmp;
        
    }

    private void intCompareBinaryOp(BinaryExprNode node) {

    }

    private void logicBinaryOp(BinaryExprNode node) {

    }

    private void processBuildInFunc(FuncExprNode funcExprNode, String targetFuncName, FuncEntity funcEntity, ExprNode thisExpr) {
        IRFunction calleeFunc;
        List<IntValue> params = new ArrayList<>();
        ExprNode param0, param1;
        VReg destReg;
        boolean oldMemAccess = memAccess;
        switch (targetFuncName) {
            case "print":
            case "println":
                param0 = funcExprNode.getParamList().get(0);
                processPrintf(targetFuncName, param0);
                break;

            case "getString":
                destReg = new VReg("getString");
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                curBlock.append(new FuncCall(curBlock, destReg, calleeFunc, params));
                funcExprNode.setReg(destReg);
                break;

            case "getInt":
                destReg = new VReg("getInt");
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                curBlock.append(new FuncCall(curBlock, destReg, calleeFunc, params));
                funcExprNode.setReg(destReg);
                break;

            case "toString":
                destReg = new VReg("toString");
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                curBlock.append(new FuncCall(curBlock, destReg, calleeFunc, params));
                funcExprNode.setReg(destReg);
                break;

            case "__array__size":
                destReg = new VReg("size");
                curBlock.append(new Load(curBlock, destReg, thisExpr.getReg(), Tools.regSize, 0));
                funcExprNode.setReg(destReg);
                break;

            case "__string__length":
                destReg = new VReg("length");
                curBlock.append(new Load(curBlock, destReg, thisExpr.getReg(), Tools.regSize, 0));
                funcExprNode.setReg(destReg);
                break;

            case "__string_substring":
                destReg = new VReg("substring");
                param0 = funcExprNode.getParamList().get(0);
                param1 = funcExprNode.getParamList().get(1);
                param0.accept(this);
                param1.accept(this);
                params.add(thisExpr.getReg());
                params.add(param0.getReg());
                params.add(param1.getReg());
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                curBlock.append(new FuncCall(curBlock, destReg, calleeFunc, params));
                funcExprNode.setReg(destReg);
                break;

            case "__string__parseInt":
                destReg = new VReg("parseInt");
                params.add(thisExpr.getReg());
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                curBlock.append(new FuncCall(curBlock, destReg, calleeFunc, params));
                funcExprNode.setReg(destReg);
                break;

            case "__string__ord":
                destReg = new VReg("ord");
                param0 = funcExprNode.getParamList().get(0);
                param0.accept(this);
                params.add(thisExpr.getReg());
                params.add(param0.getReg());
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                curBlock.append(new FuncCall(curBlock, destReg, calleeFunc, params));
                funcExprNode.setReg(destReg);
                break;
        }
        memAccess = oldMemAccess;
    }

    private void processNewArray(NewExprNode newExprNode, IntValue addr, VReg oldDestReg, int index) {
        ExprNode exprNode = newExprNode.getExprNodeList().get(index);
        VReg destReg = new VReg(null);
        boolean oldMemAccess = memAccess;
        memAccess = false;
        exprNode.accept(this);
        memAccess = oldMemAccess;
        // calc memory size
        curBlock.append(new BinaryOperation(curBlock, destReg, BinaryOperation.BinaryOp.Mul, new Immediate(newExprNode.getNewType().getRegSize()), exprNode.getReg()));
        curBlock.append(new BinaryOperation(curBlock, destReg, BinaryOperation.BinaryOp.Add, new Immediate(newExprNode.getNewType().getRegSize()), destReg));
        // alloc memory
        curBlock.append(new HeapAllocate(curBlock, destReg, destReg));

        if (newExprNode.getExprNodeList().size() - 1 > index) {
            VReg nowAddr = new VReg(null);
            VReg loopIndex = new VReg(null);
            curBlock.append(new Move(curBlock, loopIndex, new Immediate(0)));
            curBlock.append(new Move(curBlock, nowAddr, destReg));

            BasicBlock condBlock = new BasicBlock(curFunc, "new_loop_cond");
            BasicBlock bodyBlock = new BasicBlock(curFunc, "new_loop_body");
            BasicBlock afterBlock = new BasicBlock(curFunc, "new_loop_after");

            curBlock.setEnd(new Jump(curBlock, condBlock));
            curBlock = condBlock;
            VReg condReg = new VReg(null);
            curBlock.append(new Comparison(curBlock, condReg, Comparison.Compare.Greater, exprNode.getReg(), loopIndex));
            curBlock.append(new Branch(curBlock, condReg, bodyBlock, afterBlock));

            curBlock = bodyBlock;
            curBlock.append(new BinaryOperation(curBlock, nowAddr, BinaryOperation.BinaryOp.Add, nowAddr, new Immediate(newExprNode.getNewType().getRegSize())));
            processNewArray(newExprNode, nowAddr, null, index + 1);
            curBlock.append(new BinaryOperation(curBlock, loopIndex, BinaryOperation.BinaryOp.Add, loopIndex, new Immediate(1)));

            curBlock.setEnd(new Jump(curBlock, condBlock));
            curBlock = afterBlock;
        }

        if (index != 0)
            curBlock.append(new Store(curBlock, destReg, addr, Tools.regSize, 0));
        else
            curBlock.append(new Move(curBlock, oldDestReg, destReg));
    }

    private void processPrintf(String funcName, ExprNode value) {
        if (!(value.getType().getTypeName().equals("string"))) {
            throw new CompileError("print not string");
        }

        if (value instanceof BinaryExprNode) {
            processPrintf("print", ((BinaryExprNode) value).getLhs());
            processPrintf(funcName, ((BinaryExprNode) value).getRhs());
        } else {
            List<IntValue> params = new ArrayList<>();
            IRFunction calleeFunc;
            if (value instanceof FuncExprNode && ((FuncExprNode) value).getFuncEntity().getIdentifier().equals("toString")) {
                ExprNode intValue = ((FuncExprNode) value).getParamList().get(0);
                intValue.accept(this);
                params.add(intValue.getReg());
                calleeFunc = irRoot.getBuildInFuncMap().get(funcName + "ForInt");
            } else {
                value.accept(this);
                params.add(value.getReg());
                calleeFunc = irRoot.getBuildInFuncMap().get(funcName);
            }
            curBlock.append(new FuncCall(curBlock, null, calleeFunc, params));
        }
    }

    private void processAssign(IntValue dest, ExprNode src, int offset, int size, boolean accessMem) {
        if (src.getTrueBlock() == null) {
            if (accessMem)
                curBlock.append(new Store(curBlock, src.getReg(), dest, size, offset));
            else
                curBlock.append(new Move(curBlock, (Register) dest, src.getReg()));
        } else {
            BasicBlock mergeBlock = new BasicBlock(curFunc, "mergeBlock");
            if (accessMem) {
                src.getTrueBlock().append(new Store(src.getTrueBlock(), new Immediate(1), dest, Tools.regSize, offset));
                src.getFalseBlock().append(new Store(src.getFalseBlock(), new Immediate(0), dest, Tools.regSize, offset));
            } else {
                src.getTrueBlock().append(new Move(src.getTrueBlock(), (VReg) dest, new Immediate(1)));
                src.getFalseBlock().append(new Move(src.getFalseBlock(), (VReg) dest, new Immediate(0)));
            }
            if (!src.getTrueBlock().isEnd()) {
                src.getTrueBlock().setEnd(new Jump(src.getTrueBlock(), mergeBlock));
            }
            if (!src.getFalseBlock().isEnd()) {
                src.getFalseBlock().setEnd(new Jump(src.getFalseBlock(), mergeBlock));
            }
            curBlock = mergeBlock;
        }
    }

    //
    public Scope getGlobalScope() {
        return globalScope;
    }

    public Scope getCurScope() {
        return curScope;
    }

    public IRRoot getIrRoot() {
        return irRoot;
    }

    public BasicBlock getCurBlock() {
        return curBlock;
    }

    public String getCurClass() {
        return curClass;
    }

    public IRFunction getCurFunc() {
        return curFunc;
    }
}
