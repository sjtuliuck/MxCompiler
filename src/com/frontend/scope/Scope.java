package com.frontend.scope;

import com.frontend.typeRef.*;

import java.util.*;

public class Scope {
    protected Scope parentScope;
    protected List<Scope> sonScopes;

    protected Map<String, TypeRef> entities;
    protected Map<String, VarTypeRef> variables;

    public Scope() {

    }

    public Scope getParentScope() {
        return parentScope;
    }

    public List<Scope> getSonScopes() {
        return sonScopes;
    }

    public Map<String, TypeRef> getEntities() {
        return entities;
    }
}
