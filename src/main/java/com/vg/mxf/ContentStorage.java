package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContentStorage extends InterchangeObject {
    TagUUIDList Packages = null;
    TagUUIDList EssenceContainerData = null;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.18.00");

    private final static int localTags[] = new int[] { 0x1901, 0x1902, };

    @Override
    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x1901:
                    int elementCount = buf.getInt();
                    Packages = inner(new TagUUIDList(0x1901, elementCount));
                    break;
                case 0x1902:
                    elementCount = buf.getInt();
                    EssenceContainerData = inner(new TagUUIDList(0x1902, elementCount));
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
        if (Packages != null) {
            list.addAll(Arrays.asList(Packages.uuids));
        }
        if (EssenceContainerData != null) {
            list.addAll(Arrays.asList(EssenceContainerData.uuids));
        }
        return Collections.unmodifiableList(list);
    }

}
