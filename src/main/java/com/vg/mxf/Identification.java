package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;

public class Identification extends InterchangeObject {
    TagUUID ThisGenerationUID;
    TagUTF16String CompanyName;
    TagUTF16String ProductName;
    TagVersion ProductVersion;
    TagUTF16String VersionString;
    TagUUID ProductUID;
    TagDate ModificationDate;
    TagVersion ToolkitVersion;
    TagUTF16String Platform;

    private final static int localTags[] = new int[] { 0x3c01, 0x3c02, 0x3c03, 0x3c04, 0x3c05, 0x3c06, 0x3c07, 0x3c08, 0x3c09, };

    @Override
    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3c09:
                    Assert.assertEquals(16, sz);
                    ThisGenerationUID = inner(new TagUUID());
                    break;
                case 0x3c01:
                    CompanyName = inner(new TagUTF16String(sz));
                    break;
                case 0x3c02:
                    ProductName = inner(new TagUTF16String(sz));
                    break;
                case 0x3c03:
                    ProductVersion = inner(new TagVersion());
                    break;
                case 0x3c04:
                    VersionString = inner(new TagUTF16String(sz));
                    break;
                case 0x3c05:
                    Assert.assertEquals(16, sz);
                    ProductUID = inner(new TagUUID());
                    break;
                case 0x3c06:
                    ModificationDate = inner(new TagDate());
                    break;
                case 0x3c07:
                    ToolkitVersion = inner(new TagVersion());
                    break;
                case 0x3c08:
                    Platform = inner(new TagUTF16String(sz));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
