package com.vg.mxf;

public class Tag32 extends BaseTag {
    public Tag32(int expectedTag) {
        super(expectedTag);
    }

    Unsigned32 value = new Unsigned32();

    @Override
    public String toString() {
        return "" + value.get();
    }

    @Override
    public int getValueSize() {
        return 4;
    }
    
    public long get() {
        return value.get();
    }
}
