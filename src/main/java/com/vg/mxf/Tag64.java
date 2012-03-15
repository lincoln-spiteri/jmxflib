package com.vg.mxf;

public class Tag64 extends BaseTag {
    Signed64 value = new Signed64();

    @Override
    public String toString() {
        return "" + value.get();
    }

}
