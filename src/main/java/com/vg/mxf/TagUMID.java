package com.vg.mxf;

public class TagUMID extends BaseTag {
    public TagUMID(int expectedTag) {
        super(expectedTag);
    }

    UMID umid = inner(new UMID());

    @Override
    public String toString() {
        return umid.toString();
    }

    @Override
    public int getValueSize() {
        return 32;
    }
}
