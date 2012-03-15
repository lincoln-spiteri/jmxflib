package com.vg.mxf;

public class Tag16 extends BaseTag {
    Unsigned16 value = new Unsigned16();

    @Override
    public String toString() {
        return "" + value.get();
    }

}
