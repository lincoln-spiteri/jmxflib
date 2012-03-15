package com.vg.mxf;

public class LocalTagKey extends PackedStruct {
    Unsigned16 localTag = new Unsigned16();
    Key key = inner(new Key());

    @Override
    public String toString() {
        return String.format("%04X: %s", localTag.get(), key.toString());
    }
}
