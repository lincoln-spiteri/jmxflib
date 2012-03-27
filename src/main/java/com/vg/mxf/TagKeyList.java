package com.vg.mxf;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TagKeyList extends BaseTag {
    Unsigned32 elementCount = new Unsigned32();
    Unsigned32 elementSize = new Unsigned32();
    Key[] keys = null;

    public TagKeyList(int expectedTag, int elementCount) {
        super(expectedTag);
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

    @Override
    public int getValueSize() {
        return 8 + getCount() * 16;
    }

    int getCount() {
        return keys != null ? keys.length : 0;
    }

    @Override
    void updateFields() {
        super.updateFields();
        elementCount.set(getCount());
        elementSize.set(16);
    }

}
