package com.vg.mxf;

import static com.vg.mxf.Key.key;
import static java.util.Arrays.asList;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class Sequence extends StructuralComponent {
    TagUUIDList StructuralComponents;

    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.0F.00");

    private final static int localTags[] = new int[] { 0x1001 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x1001:
                    int elementCount = buf.getInt();
                    StructuralComponents = inner(new TagUUIDList(0x1001, elementCount));
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
        return StructuralComponents != null ? asList(StructuralComponents.uuids) : asList(new UUID[0]);
    }

}
