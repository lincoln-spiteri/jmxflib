package com.vg.mxf;

public class TagDate extends BaseTag {
    Signed16 year = new Signed16();
    Unsigned8 month = new Unsigned8();
    Unsigned8 day = new Unsigned8();
    Unsigned8 hour = new Unsigned8();
    Unsigned8 minute = new Unsigned8();
    Unsigned8 sec = new Unsigned8();
    Unsigned8 msec4 = new Unsigned8();

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d.%03d", year.get(), month.get(), day.get(), hour.get(), minute.get(), sec.get(), msec4.get() * 4);
    }
}
