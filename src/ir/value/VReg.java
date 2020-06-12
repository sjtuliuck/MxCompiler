package ir.value;

import ir.IRVisitor;

public class VReg extends Register {
    private String name;
    private PReg forcedPReg = null;

    public VReg(String name) {
        this.name = name;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntValue copy() {
        return null;
    }

    //
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PReg getForcedPReg() {
        return forcedPReg;
    }

    public void setForcedPReg(PReg forcedPReg) {
        this.forcedPReg = forcedPReg;
    }
}
