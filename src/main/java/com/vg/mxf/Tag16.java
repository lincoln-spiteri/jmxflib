package com.vg.mxf;

public class Tag16 extends BaseTag {
    public Tag16(int expectedTag) {
        super(expectedTag);
    }

    Unsigned16 value = new Unsigned16();

    @Override
    public String toString() {
        return "" + value.get();
    }

    @Override
    public int getValueSize() {
        return 2;
    }

}
