package com.vg.mxf;

public class TagUUID extends BaseTag {
    UUID uuid = inner(new UUID());

    @Override
    public String toString() {
        return uuid.toString();
    }
}
