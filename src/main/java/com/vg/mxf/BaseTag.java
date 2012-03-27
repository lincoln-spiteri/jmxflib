package com.vg.mxf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class BaseTag extends PackedStruct {
    final static boolean DEBUG = false;
    Unsigned16 tag = new Unsigned16();
    Unsigned16 sz = new Unsigned16();
    private final int expectedTag;

    public BaseTag(int expectedTag) {
        this.expectedTag = expectedTag;
    }

    public int getExpectedTag() {
        return expectedTag;
    }

    public abstract int getValueSize();

    public int getSize() {
        return sz.get();
    }

    @Override
    public Element toXml(Document doc, Element xml) {
        if (DEBUG) {
            xml.setAttribute("tag", String.format("%04X", tag.get()));
        }
        xml.setAttribute("value", toString());
        return xml;
    }

    @Override
    void updateFields() {
        super.updateFields();
        tag.set(expectedTag);
        sz.set(getValueSize());
    }

}
