package com.vg.mxf;

public class TagUUID extends BaseTag {
    public TagUUID(int expectedTag) {
        super(expectedTag);
    }

    UUID uuid = inner(new UUID());

    @Override
    public String toString() {
        return uuid.toString();
    }

    @Override
    public int getValueSize() {
        return 16;
    }
}
