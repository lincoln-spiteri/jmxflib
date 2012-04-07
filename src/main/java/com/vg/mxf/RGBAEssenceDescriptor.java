package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RGBAEssenceDescriptor extends GenericPictureEssenceDescriptor {
    Tag32 ComponentMaxRef;
    Tag32 ComponentMinRef;
    Tag32 AlphaMaxRef;
    Tag32 AlphaMinRef;
    Tag8 ScanningDirection;
    TagValue PixelLayout;
    TagValue Palette;
    TagValue PaletteLayout;
    TagValue ffff;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.29.00");

    private final static int localTags[] = new int[] { 0x3401, 0x3403, 0x3404, 0x3405, 0x3406, 0x3407, 0x3408, 0x3409, 0xffff };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x3406:
                    ComponentMaxRef = inner(new Tag32(0x3406));
                    break;
                case 0x3407:
                    ComponentMaxRef = inner(new Tag32(0x3407));
                    break;
                case 0x3408:
                    AlphaMaxRef = inner(new Tag32(0x3408));
                    break;
                case 0x3409:
                    AlphaMinRef = inner(new Tag32(0x3409));
                    break;
                case 0x3405:
                    ScanningDirection = inner(new Tag8(0x3405));
                    break;
                case 0x3401:
                    PixelLayout = inner(new TagValue(0x3401, sz));
                    break;
                case 0x3403:
                    Palette = inner(new TagValue(0x3309, sz));
                    break;
                case 0x3404:
                    PaletteLayout = inner(new TagValue(0x3404, sz));
                    break;
                case 0xffff:
                    ffff = inner(new TagValue(0xffff, sz));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

}
