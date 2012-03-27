package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenericPackage extends InterchangeObject {
    TagUMID PackageUID;
    TagUTF16String Name;
    TagDate PackageCreationDate;
    TagDate PackageModifiedDate;
    TagUUIDList Tracks;

    private final static int localTags[] = new int[] { 0x4401, 0x4402, 0x4403, 0x4404, 0x4405 };

    @Override
    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x4401:
                    PackageUID = inner(new TagUMID(0x4401));
                    break;
                case 0x4402:
                    Name = inner(new TagUTF16String(0x4402, sz));
                    break;
                case 0x4405:
                    PackageCreationDate = inner(new TagDate(0x4405));
                    break;
                case 0x4404:
                    PackageModifiedDate = inner(new TagDate(0x4404));
                    break;
                case 0x4403:
                    int elementCount = buf.getInt();
                    Tracks = inner(new TagUUIDList(0x4403, elementCount));
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
        if (Tracks != null) {
            list.addAll(Arrays.asList(Tracks.uuids));
        }
        return Collections.unmodifiableList(list);
    }
}
