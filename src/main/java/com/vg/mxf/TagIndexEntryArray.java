package com.vg.mxf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TagIndexEntryArray extends BaseTag {
    Unsigned32 elementCount = new Unsigned32();
    Unsigned32 elementSize = new Unsigned32();
    IndexEntry[] entries;

    public TagIndexEntryArray(int expectedTag, int elementCount) {
        super(expectedTag);
        entries = array(new IndexEntry[elementCount]);
    }

    @Override
    public Element toXml(Document doc, Element xml) {
        if (DEBUG) {
            xml.setAttribute("tag", String.format("%04X", tag.get()));
        }
        for (int i = 0; i < entries.length; i++) {
            xml.appendChild(entries[i].toXml(doc, doc.createElement("index")));
        }
        return xml;
    }

    @Override
    public int getValueSize() {
        return 8 + IndexEntry.sizeof * getCount();
    }

    int getCount() {
        return entries != null ? entries.length : 0;
    }

    @Override
    void updateFields() {
        super.updateFields();
        elementCount.set(getCount());
        elementSize.set(IndexEntry.sizeof);
    }
}
