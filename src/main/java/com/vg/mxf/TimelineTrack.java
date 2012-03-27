package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.lang3.math.Fraction;

public class TimelineTrack extends GenericTrack {
    TagRational EditRate;
    Tag64 Origin;
    public static final Key Key = key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.3B.00");

    private final static int localTags[] = new int[] { 0x4b01, 0x4b02 };

    boolean handleTag(int localTag, int sz, ByteBuffer buf) {
        if (!super.handleTag(localTag, sz, buf)) {
            if (Arrays.binarySearch(localTags, localTag) >= 0) {
                switch (localTag) {
                case 0x4b01:
                    EditRate = inner(new TagRational(0x4b01));
                    break;
                case 0x4b02:
                    Origin = inner(new Tag64(0x4b02));
                    break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

    public Fraction getEditRate() {
        return this.EditRate != null ? Fraction.getFraction(EditRate.num.get(), EditRate.den.get()) : Fraction.ONE;
    }

}
