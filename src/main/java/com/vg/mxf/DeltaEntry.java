package com.vg.mxf;

public class DeltaEntry extends PackedStruct {
    public static final int sizeof = new DeltaEntry().size();
    Signed8 PosTableIndex = new Signed8();
    Unsigned8 Slice = new Unsigned8();
    Unsigned32 ElementDelta = new Unsigned32();
}
