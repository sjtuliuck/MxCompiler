package ir.value;

public abstract class StaticData extends Register {
    private String name;
    private int size;

    public StaticData(String name, int size) {
        this.name = name;
        this.size = size;
    }


    @Override
    public StaticData copy() {
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
