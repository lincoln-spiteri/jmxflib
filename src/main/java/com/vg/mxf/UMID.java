package com.vg.mxf;

public class UMID extends PackedStruct {
    UUID hi = inner(new UUID());
    UUID lo = inner(new UUID());

    @Override
    public String toString() {
        return hi.toString() + "-" + lo.toString();
    }
}
