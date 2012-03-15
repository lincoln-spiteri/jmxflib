package com.vg.mxf;

public class TagUMID extends BaseTag {
    UMID umid = inner(new UMID());

    @Override
    public String toString() {
        return umid.toString();
    }
}
