package com.vg.mxf;

import java.util.Arrays;

public class TagUUIDList extends BaseTag {
    Unsigned32 elementCount = new Unsigned32();
    Unsigned32 elementSize = new Unsigned32();
    UUID[] uuids = null;

    public TagUUIDList(int elementCount) {
        uuids = array(new UUID[elementCount]);
    }

    @Override
    public String toString() {
        return Arrays.toString(uuids);
    }
}
