package com.vg.mxf;

public class Tag8 extends BaseTag {
    public Tag8(int expectedTag) {
        super(expectedTag);
    }

    Unsigned8 value = new Unsigned8();

    @Override
    public String toString() {
        return "" + value.get();
    }

    @Override
    public int getValueSize() {
        return 1;
    }

    public int get() {
        return value.get();
    }

}
