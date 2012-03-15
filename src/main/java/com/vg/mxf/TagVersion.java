package com.vg.mxf;

public class TagVersion extends BaseTag {
    Unsigned16 major = new Unsigned16();
    Unsigned16 minor = new Unsigned16();
    Unsigned16 patch = new Unsigned16();
    Unsigned16 build = new Unsigned16();
    Unsigned16 released = new Unsigned16();

    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d.%d", major.get(), minor.get(), patch.get(), build.get(), released.get());
    }
}
