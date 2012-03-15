package com.vg.mxf;

public class PrimerPack extends MxfValue {
    Unsigned32 elementCount = new Unsigned32();
    Unsigned32 elementSize = new Unsigned32();
    LocalTagKey[] ltks;

    @Override
    public void parse() {
        ltks = array(new LocalTagKey[(int) elementCount.get()]);
    }

}
