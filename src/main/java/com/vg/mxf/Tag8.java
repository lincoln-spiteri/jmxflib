package com.vg.mxf;

public class Tag8 extends BaseTag {
    Unsigned8 value = new Unsigned8();

    @Override
    public String toString() {
        return "" + value.get();
    }

}
