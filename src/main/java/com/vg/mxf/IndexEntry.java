package com.vg.mxf;

public class IndexEntry extends PackedStruct {
    public final static int sizeof = new IndexEntry().size();
    Signed8 TemporalOffset = new Signed8();
    Signed8 KeyFrameOffset = new Signed8();
    Unsigned8 Flags = new Unsigned8();
    Signed64 StreamOffset = new Signed64();

}
