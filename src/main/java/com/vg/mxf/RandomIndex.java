package com.vg.mxf;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RandomIndex extends MxfValue {
    private int overallLength;
    private IntBuffer sids;
    private LongBuffer offsets;

    @Override
    public void parse() {
        ByteBuffer buf = getAllContent();
        overallLength = buf.getInt(buf.limit() - 4);
        buf.limit(buf.limit() - 4);
        int count = buf.limit() / 12;
        sids = IntBuffer.allocate(count);
        offsets = LongBuffer.allocate(count);
        while (buf.hasRemaining()) {
            int sid = buf.getInt();
            long byteOffset = buf.getLong() & 0xffffffffL;
            sids.put(sid);
            offsets.put(byteOffset);
        }
        sids.clear();
        offsets.clear();
    }

    @Override
    public Element toXml(Document doc, Element xml) {
        IntBuffer s = sids.duplicate();
        LongBuffer o = offsets.duplicate();
        while (s.hasRemaining() && o.hasRemaining()) {
            Element e = doc.createElement("ridx");
            e.setAttribute("BodySID", "" + s.get());
            e.setAttribute("ByteOffset", "" + o.get());
            xml.appendChild(e);
        }
        xml.setAttribute("OverallLength", "" + overallLength);
        return xml;
    }
}
