package com.vg.mxf;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;

public class JPEG2000PictureSubDescriptor extends InterchangeObject {

    Tag16 Rsiz;
    Tag32 Xsiz;
    Tag32 Ysiz;
    Tag32 XOsiz;
    Tag32 YOsiz;
    Tag32 XTsiz;
    Tag32 YTsiz;
    Tag32 XTOsiz;
    Tag32 YTOsiz;
    Tag16 Csiz;
    TagValue PictureComponentSizing;

    private final static int localTags[] = new int[] { 0x6104, 0x6105, 0x6106, 0x6107, 0x6108, 0x6109, 0x610a, 0x610b, 0x610c,
            0x610d, 0x610e, };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x6104:
                    Assert.assertEquals(2, sz);
                    Rsiz = inner(new Tag16());
                    break;
                case 0x6105:
                    Assert.assertEquals(4, sz);
                    Xsiz = inner(new Tag32());
                    break;
                case 0x6106:
                    Assert.assertEquals(4, sz);
                    Ysiz = inner(new Tag32());
                    break;
                case 0x6107:
                    Assert.assertEquals(4, sz);
                    XOsiz = inner(new Tag32());
                    break;
                case 0x6108:
                    Assert.assertEquals(4, sz);
                    YOsiz = inner(new Tag32());
                    break;
                case 0x6109:
                    Assert.assertEquals(4, sz);
                    XTsiz = inner(new Tag32());
                    break;
                case 0x610a:
                    Assert.assertEquals(4, sz);
                    YTsiz = inner(new Tag32());
                    break;
                case 0x610b:
                    Assert.assertEquals(4, sz);
                    XTOsiz = inner(new Tag32());
                    break;
                case 0x610c:
                    Assert.assertEquals(4, sz);
                    YTOsiz = inner(new Tag32());
                    break;
                case 0x610d:
                    Assert.assertEquals(2, sz);
                    Csiz = inner(new Tag16());
                    break;
                case 0x610e:
                    PictureComponentSizing = inner(new TagValue(sz));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
