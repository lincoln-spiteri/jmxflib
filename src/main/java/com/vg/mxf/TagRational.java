package com.vg.mxf;

public class TagRational extends BaseTag {
    public TagRational(int expectedTag) {
        super(expectedTag);
    }

    Signed32 num = new Signed32();
    Signed32 den = new Signed32();

    @Override
    public String toString() {
        return "" + num.get() + "/" + den.get();
    }

    @Override
    public int getValueSize() {
        return 8;
    }

}
