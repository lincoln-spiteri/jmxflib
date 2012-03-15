package com.vg.mxf;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TagKeyList extends BaseTag {
    Unsigned32 elementCount = new Unsigned32();
    Unsigned32 elementSize = new Unsigned32();
    Key[] keys = null;

    public TagKeyList(int elementCount) {
        keys = array(new Key[elementCount]);
    }

    @Override
    public String toString() {
        return Arrays.toString(keys);
    }

    @Override
    public Element toXml(Document doc, Element xml) {
        if (DEBUG) {
            xml.setAttribute("tag", String.format("%04X", tag.get()));
        }
        for (int i = 0; i < keys.length; i++) {
            xml.appendChild(keys[i].toXml(doc, doc.createElement("key")));
        }
        return xml;
    }

}
