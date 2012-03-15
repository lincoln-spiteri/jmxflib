package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Sequence extends StructuralComponent {
    TagUUIDList StructuralComponents;

    private final static int localTags[] = new int[] { 0x1001 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x1001:
                    int elementCount = buf.getInt();
                    StructuralComponents = inner(new TagUUIDList(elementCount));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
