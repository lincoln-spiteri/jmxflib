package com.vg.mxf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class BaseTag extends PackedStruct {
    final static boolean DEBUG = false;
    Unsigned16 tag = new Unsigned16();
    Unsigned16 sz = new Unsigned16();

    @Override
    public Element toXml(Document doc, Element xml) {
        if (DEBUG) {
            xml.setAttribute("tag", String.format("%04X", tag.get()));
        }
        xml.setAttribute("value", toString());
        return xml;
    }

}
