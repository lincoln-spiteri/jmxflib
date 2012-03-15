package com.vg.mxf;

public class Tag32 extends BaseTag {
    Unsigned32 value = new Unsigned32();

    @Override
    public String toString() {
        return "" + value.get();
    }
}
