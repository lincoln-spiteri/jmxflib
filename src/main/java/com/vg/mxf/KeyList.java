package com.vg.mxf;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyList extends PackedStruct {
    Unsigned32 elementCount = new Unsigned32();
    Unsigned32 elementSize = new Unsigned32();
    Key[] keys;

    public KeyList(int elementCount) {
        keys = array(new Key[elementCount]);
    }

    @Override
    public String toString() {
        return Arrays.toString(keys);
    }

    public Key get(int i) {
        return keys[i];
    }

    @Override
    public Element toXml(Document doc, Element xml) {
        for (int i = 0; i < keys.length; i++) {
            xml.appendChild(keys[i].toXml(doc, doc.createElement("key")));
        }
        return xml;
    }
}
