package com.vg.mxf;

import static com.vg.mxf.Key.key;

public class PrimerPack extends MxfValue {
    Unsigned32 elementCount = new Unsigned32();
    Unsigned32 elementSize = new Unsigned32();
    LocalTagKey[] ltks;
    public static final Key Key = key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.05.01.00");

    @Override
    public void parse() {
        ltks = array(new LocalTagKey[(int) elementCount.get()]);
    }

}
