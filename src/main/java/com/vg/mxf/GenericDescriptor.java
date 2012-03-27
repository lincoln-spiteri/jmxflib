package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenericDescriptor extends InterchangeObject {
    TagUUIDList Locators;
    TagUUIDList Unknown;

    private final static int localTags[] = new int[] { 0x2f01, 0x2f02 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x2f01:
                    int count = buf.getInt();
                    Locators = inner(new TagUUIDList(0x2f01,count));
                    break;
                case 0x2f02:
                    count = buf.getInt();
                    Unknown = inner(new TagUUIDList(0x2f02, count));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }
    
    @Override
    public List<UUID> getReferencedUIDs() {
        List<UUID> list = new ArrayList<UUID>();
        if (Locators != null) {
            list.addAll(Arrays.asList(Locators.uuids));
        }
        if (Unknown != null) {
            list.addAll(Arrays.asList(Unknown.uuids));
        }
        return Collections.unmodifiableList(list);
    }

}
