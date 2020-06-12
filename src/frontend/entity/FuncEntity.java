package frontend.entity;

import ast.FuncDefNode;
import ast.VarNode;
import frontend.Scope;
import frontend.type.FuncType;
import frontend.type.NullType;
import frontend.type.Type;
import utility.CompileError;

import java.util.ArrayList;
import java.util.List;

public class FuncEntity extends Entity {
    private Type retType;
    private boolean inClass = false;
    private String classIdentifier = null;
    private boolean buildInFunc = false;
    private Scope funcScope;
    private List<VarEntity> paramList;

    public FuncEntity(String identifier, Type type) {
        super(identifier, type);
    }

    public FuncEntity(FuncDefNode funcDefNode, Scope father) {
        super(funcDefNode.getIdentifier(), new FuncType(funcDefNode.getIdentifier()));
        addFuncNode(funcDefNode, father);
    }

    // in class
    public FuncEntity(FuncDefNode funcDefNode, Scope father, String classIdentifier) {
        super(funcDefNode.getIdentifier(), new FuncType(funcDefNode.getIdentifier()));
        this.inClass = true;
        this.classIdentifier = classIdentifier;
        addFuncNode(funcDefNode, father);
    }

    private void addFuncNode(FuncDefNode funcDefNode, Scope father) {
        if (funcDefNode.getRetType() != null) {
            this.retType = funcDefNode.getRetType().getType();
        } else {
            throw new CompileError("Function return type is NULL!!!");
        }
        funcScope = new Scope(father);
        paramList = new ArrayList<VarEntity>();
        if (funcDefNode.getParamList() != null) {
            for (VarNode varNode : funcDefNode.getParamList()) {
                VarEntity varEntity = new VarEntity(varNode);
                funcScope.addVar(varEntity);
                paramList.add(varEntity);
            }
        }
    }

    public boolean isConstructor() {
//        return retType.getTypeName().equals("null");
        return retType instanceof NullType;
    }

    public Type getRetType() {
        return retType;
    }

    public boolean isInClass() {
        return inClass;
    }

    public String getClassIdentifier() {
        return classIdentifier;
    }

    public boolean isBuildInFunc() {
        return buildInFunc;
    }

    public Scope getFuncScope() {
        return funcScope;
    }

    public List<VarEntity> getParamList() {
        return paramList;
    }

    public void setRetType(Type retType) {
        this.retType = retType;
    }

    public void setInClass(boolean inClass) {
        this.inClass = inClass;
    }

    public void setClassIdentifier(String classIdentifier) {
        this.classIdentifier = classIdentifier;
    }

    public void setBuildInFunc(boolean buildInFunc) {
        this.buildInFunc = buildInFunc;
    }

    public void setFuncScope(Scope funcScope) {
        this.funcScope = funcScope;
    }

    public void setParamList(List<VarEntity> paramList) {
        this.paramList = paramList;
    }
}
