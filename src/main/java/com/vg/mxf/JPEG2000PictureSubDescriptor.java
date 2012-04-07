package com.vg.mxf;

import static com.vg.mxf.Key.key;

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
    TagValue fff1;
    TagValue fff2;
    TagValue fff3;
    TagValue fff4;
    TagValue fff5;
    TagValue fff6;
    TagValue fff7;
    TagValue fff8;
    TagValue fff9;
    TagValue fffa;
    TagValue fffb;
    TagValue fffc;
    TagValue fffe;
    TagValue fffd;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.5A.00");

    private final static int localTags[] = new int[] { 0x6104, 0x6105, 0x6106, 0x6107, 0x6108, 0x6109, 0x610a, 0x610b,
            0x610c, 0x610d, 0x610e, 0xfff1, 0xfff2, 0xfff3, 0xfff4, 0xfff5, 0xfff6, 0xfff7, 0xfff8, 0xfff9, 0xfffa,
            0xfffb, 0xfffc, 0xfffd, 0xfffe };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x6104:
                case 0xfffe:
                    Rsiz = inner(new Tag16(0x6104));
                    break;
                case 0x6105:
                case 0xfffd:
                    Assert.assertEquals(4, sz);
                    Xsiz = inner(new Tag32(0x6105));
                    break;
                case 0x6106:
                case 0xfffc:
                    Assert.assertEquals(4, sz);
                    Ysiz = inner(new Tag32(0x6106));
                    break;
                case 0x6107:
                case 0xfffb:
                    Assert.assertEquals(4, sz);
                    XOsiz = inner(new Tag32(0x6107));
                    break;
                case 0x6108:
                case 0xfffa:
                    Assert.assertEquals(4, sz);
                    YOsiz = inner(new Tag32(0x6108));
                    break;
                case 0x6109:
                case 0xfff9:
                    Assert.assertEquals(4, sz);
                    XTsiz = inner(new Tag32(0x6109));
                    break;
                case 0x610a:
                case 0xfff8:
                    Assert.assertEquals(4, sz);
                    YTsiz = inner(new Tag32(0x610a));
                    break;
                case 0x610b:
                case 0xfff7:
                    Assert.assertEquals(4, sz);
                    XTOsiz = inner(new Tag32(0x610b));
                    break;
                case 0x610c:
                case 0xfff6:
                    Assert.assertEquals(4, sz);
                    YTOsiz = inner(new Tag32(0x610c));
                    break;
                case 0x610d:
                case 0xfff5:
                    Assert.assertEquals(2, sz);
                    Csiz = inner(new Tag16(0x610d));
                    break;
                case 0x610e:
                case 0xfff4:
                    PictureComponentSizing = inner(new TagValue(0x610e, sz));
                    break;
                case 0xfff1:
                    fff1 = inner(new TagValue(0xfff1, sz));
                    break;
                case 0xfff2:
                    fff2 = inner(new TagValue(0xfff2, sz));
                    break;
                case 0xfff3:
                    fff3 = inner(new TagValue(0xfff3, sz));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
