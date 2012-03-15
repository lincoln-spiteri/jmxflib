package com.vg.mxf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TagKey extends BaseTag {
    Key key = inner(new Key());

    @Override
    public String toString() {
        return key.toString();
    }

    @Override
    public Element toXml(Document doc, Element xml) {
        if (DEBUG) {
            xml.setAttribute("tag", String.format("%04X", tag.get()));
        }
        xml.appendChild(key.toXml(doc, doc.createElement("key")));
        return xml;
    }
}
