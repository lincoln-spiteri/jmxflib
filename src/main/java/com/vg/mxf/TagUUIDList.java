package com.vg.mxf;

import java.util.Arrays;

public class TagUUIDList extends BaseTag {
    Unsigned32 elementCount = new Unsigned32();
    Unsigned32 elementSize = new Unsigned32();
    UUID[] uuids = null;

    public TagUUIDList(int expectedTag, int elementCount) {
        super(expectedTag);
        uuids = array(new UUID[elementCount]);
    }

    @Override
    public String toString() {
        return Arrays.toString(uuids);
    }

    @Override
    public int getValueSize() {
        return 8 + 16 * (uuids != null ? uuids.length : 0);
    }

    int getCount() {
        return uuids != null ? uuids.length : 0;
    }

    @Override
    void updateFields() {
        super.updateFields();
        elementCount.set(getCount());
        elementSize.set(16);
    }
}
