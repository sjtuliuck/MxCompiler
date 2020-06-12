package ir.value;

import ir.IRVisitor;

public class StaticStr extends StaticData {
    private String value;

    public StaticStr(String value, int size) {
        super(value, size);
        this.value = value;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    //
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
