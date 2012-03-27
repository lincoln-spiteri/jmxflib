package com.vg.mxf;

import static com.vg.mxf.Key.key;

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
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.30.00");

    private final static int localTags[] = new int[] { 0x3c01, 0x3c02, 0x3c03, 0x3c04, 0x3c05, 0x3c06, 0x3c07, 0x3c08,
            0x3c09, };

    @Override
    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3c09:
                    Assert.assertEquals(16, sz);
                    ThisGenerationUID = inner(new TagUUID(0x3c09));
                    break;
                case 0x3c01:
                    CompanyName = inner(new TagUTF16String(0x3c01, sz));
                    break;
                case 0x3c02:
                    ProductName = inner(new TagUTF16String(0x3c02, sz));
                    break;
                case 0x3c03:
                    ProductVersion = inner(new TagVersion(0x3c03));
                    break;
                case 0x3c04:
                    VersionString = inner(new TagUTF16String(0x3c04, sz));
                    break;
                case 0x3c05:
                    Assert.assertEquals(16, sz);
                    ProductUID = inner(new TagUUID(0x3c05));
                    break;
                case 0x3c06:
                    ModificationDate = inner(new TagDate(0x3c06));
                    break;
                case 0x3c07:
                    ToolkitVersion = inner(new TagVersion(0x3c07));
                    break;
                case 0x3c08:
                    Platform = inner(new TagUTF16String(0x3c08, sz));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
