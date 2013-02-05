package com.vg.mxf;

import static com.vg.mxf.Key.key;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.Arrays;

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
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.5A.00");

    private final static int localTags[] = new int[] { 0x6104, 0x6105, 0x6106, 0x6107, 0x6108, 0x6109, 0x610a, 0x610b,
            0x610c, 0x610d, 0x610e };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x6104:
                    Rsiz = inner(new Tag16(0x6104));
                    break;
                case 0x6105:
                    assertEquals(4, sz);
                    Xsiz = inner(new Tag32(0x6105));
                    break;
                case 0x6106:
                    assertEquals(4, sz);
                    Ysiz = inner(new Tag32(0x6106));
                    break;
                case 0x6107:
                    assertEquals(4, sz);
                    XOsiz = inner(new Tag32(0x6107));
                    break;
                case 0x6108:
                    assertEquals(4, sz);
                    YOsiz = inner(new Tag32(0x6108));
                    break;
                case 0x6109:
                    assertEquals(4, sz);
                    XTsiz = inner(new Tag32(0x6109));
                    break;
                case 0x610a:
                    assertEquals(4, sz);
                    YTsiz = inner(new Tag32(0x610a));
                    break;
                case 0x610b:
                    assertEquals(4, sz);
                    XTOsiz = inner(new Tag32(0x610b));
                    break;
                case 0x610c:
                    assertEquals(4, sz);
                    YTOsiz = inner(new Tag32(0x610c));
                    break;
                case 0x610d:
                    assertEquals(2, sz);
                    Csiz = inner(new Tag16(0x610d));
                    break;
                case 0x610e:
                    PictureComponentSizing = inner(new TagValue(0x610e, sz));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
