package com.ir;

import com.ir.value.*;

import java.util.*;

public class IRRoot {
    private Map<String, IRFunction> functionMap = new HashMap<>();
    private Map<String, IRFunction> buildInFuncMap = new HashMap<>();
    private Map<String, StaticStr> staticStrMap = new HashMap<>();
    private List<StaticData> staticDataList = new ArrayList<>();

    private List<String> buildInList = new ArrayList<>();

    public IRRoot() {
        buildInList.add("print");
        buildInList.add("println");
        buildInList.add("printInt");
        buildInList.add("printlnInt");
        buildInList.add("getString");
        buildInList.add("getInt");
        buildInList.add("toString");
        //
        buildInList.add("__array__size");
        buildInList.add("__string__length");
        buildInList.add("__string__substring");
        buildInList.add("__string__parseInt");
        buildInList.add("__string__ord");
        //
        buildInList.add("__string__link");
        buildInList.add("__string__less");
        buildInList.add("__string__leq");
        buildInList.add("__string__greater");
        buildInList.add("__string__geq");
        buildInList.add("__string__equal");
        buildInList.add("__string__neq");

        for (String buildInFuncName : buildInList) {
            buildInFuncMap.put(buildInFuncName, new IRFunction(buildInFuncName));
        }
    }

    public void addFunction(IRFunction function) {
        functionMap.put(function.getName(), function);
    }

    public void addStaticData(StaticData staticData) {
        staticDataList.add(staticData);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    //
    public Map<String, IRFunction> getFunctionMap() {
        return functionMap;
    }

    public void setFunctionMap(Map<String, IRFunction> functionMap) {
        this.functionMap = functionMap;
    }

    public Map<String, IRFunction> getBuildInFuncMap() {
        return buildInFuncMap;
    }

    public void setBuildInFuncMap(Map<String, IRFunction> buildInFuncMap) {
        this.buildInFuncMap = buildInFuncMap;
    }

    public Map<String, StaticStr> getStaticStrMap() {
        return staticStrMap;
    }

    public void setStaticStrMap(Map<String, StaticStr> staticStrMap) {
        this.staticStrMap = staticStrMap;
    }

    public List<StaticData> getStaticDataList() {
        return staticDataList;
    }

    public void setStaticDataList(List<StaticData> staticDataList) {
        this.staticDataList = staticDataList;
    }

    public List<String> getBuildInList() {
        return buildInList;
    }

    public void setBuildInList(List<String> buildInList) {
        this.buildInList = buildInList;
    }
}
