package com.vg.mxf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vg.io.SeekableFileInputStream;
import com.vg.io.SeekableInputStream;
import com.vg.util.XmlUtil;

public class MxfStructure {
    private HeaderPartitionPack headerPartitionPack;
    private KLV footerKLV;
    private FooterPartitionPack footerPartitionPack;
    public TreeMap<KLV, MxfValue> headerKLVs = new TreeMap<KLV, MxfValue>();
    public TreeMap<KLV, MxfValue> bodyKLVs = new TreeMap<KLV, MxfValue>();
    public TreeMap<KLV, IndexTable> indexKLVs = new TreeMap<KLV, IndexTable>();
    public TreeMap<KLV, MxfValue> allKLVs = new TreeMap<KLV, MxfValue>();
    public TreeMap<UUID, MxfValue> uuidIndex = new TreeMap<UUID, MxfValue>();

    public long getBodyOffset() {
        KLV key = headerKLVs.lastEntry().getKey();
        return key.dataOffset + key.len;
    }

    public MxfValue getValue(UUID uuid) {
        return uuidIndex.get(uuid);
    }

    BodyPartitionPack getBodyPartitionPack(int bodySID) {
        for (Entry<KLV, MxfValue> entry : bodyKLVs.entrySet()) {
            BodyPartitionPack value = (BodyPartitionPack) entry.getValue();
            if (value.BodySID.get() == bodySID) {
                return value;
            }
        }
        return null;
    }

    EssenceContainerData getEssenceContainerData() {
        for (Entry<KLV, MxfValue> entry : headerKLVs.entrySet()) {
            KLV key2 = entry.getKey();
            if (EssenceContainerData.Key.equals(key2.key)) {
                return (EssenceContainerData) entry.getValue();
            }
        }
        return null;
    }

    TreeMap<Long, BodyPartitionPack> bppIndex = new TreeMap<Long, BodyPartitionPack>();

    void init() {
        allKLVs.putAll(headerKLVs);
        allKLVs.putAll(bodyKLVs);
        allKLVs.putAll(indexKLVs);
        FooterPartitionPack fpp = getFooterPartitionPack();
        if (fpp != null) {
            allKLVs.put(footerKLV, fpp);
        }
        for (Entry<KLV, MxfValue> entry : allKLVs.entrySet()) {
            MxfValue value = entry.getValue();
            UUID instanceUID = value.getInstanceUID();
            uuidIndex.put(instanceUID, value);
        }

    }

    Set<Entry<KLV, MxfValue>> entries() {
        Set<Entry<KLV, MxfValue>> entrySet = allKLVs.entrySet();
        return entrySet;
    }

    public HeaderPartitionPack getHeaderPartitionPack() {
        return headerPartitionPack;
    }

    public void setHeaderPartitionPack(HeaderPartitionPack headerPartitionPack) {
        this.headerPartitionPack = headerPartitionPack;
    }

    public KLV getHeaderKLV() {
        return this.headerKLVs.firstEntry().getKey();
    }

    public static MxfStructure readStructure(SeekableInputStream in) throws IOException {
        MxfStructure s = new MxfStructure();
        s.headerKLVs = MxfStructure.readHeader(in);

        HeaderPartitionPack headerPartitionPack = (HeaderPartitionPack) s.headerKLVs.firstEntry().getValue();
        s.setHeaderPartitionPack(headerPartitionPack);
        if (headerPartitionPack != null && headerPartitionPack.HeaderByteCount.get() > 0) {
            //read footer
            long footerOffset = headerPartitionPack.FooterPartition.get();
            if (headerPartitionPack.IndexByteCount.get() > 0 && headerPartitionPack.IndexSID.get() > 0) {
                KLV kl = KLV.readKL(in);
                assertEquals(IndexTable.Key, kl.key);
                IndexTable indexTable = MxfValue.parseValue(in, kl, IndexTable.class);
                s.indexKLVs.put(kl, indexTable);
            }
            if (footerOffset != 0) {
                assertTrue(footerOffset > 0);
                in.seek(footerOffset);
                KLV kl = KLV.readKL(in);
                assertTrue(FooterPartitionPack.Key.matches(kl.key));
                FooterPartitionPack footerPartitionPack = MxfValue.parseValue(in, kl, FooterPartitionPack.class);
                s.footerKLV = kl;
                s.setFooterPartitionPack(footerPartitionPack);
                long headerPPOffset = headerPartitionPack.ThisPartition.get();
                long previousPartitionOffset = footerPartitionPack.PreviousPartition.get();

                //read backwards partitions and indexes
                do {
                    in.seek(previousPartitionOffset);
                    kl = KLV.readKL(in);
                    if (BodyPartitionPack.Key.matches(kl.key)) {
                        BodyPartitionPack bpp = MxfValue.parseValue(in, kl, BodyPartitionPack.class);
                        //sanity check: make sure partition offsets are actually going backwards
                        assertTrue(bpp.PreviousPartition.get() < previousPartitionOffset);
                        s.bodyKLVs.put(kl, bpp);
                        previousPartitionOffset = bpp.PreviousPartition.get();
                        if (bpp.IndexByteCount.get() > 0 && bpp.IndexSID.get() > 0) {
                            //index follows
                            kl = KLV.readKL(in);
                            assertEquals(IndexTable.Key, kl.key);
                            IndexTable indexTable = MxfValue.parseValue(in, kl, IndexTable.class);
                            s.indexKLVs.put(kl, indexTable);
                        }
                    } else if (HeaderPartitionPack.Key.matches(kl.key)) {
                        break;
                    } else {
                        throw new IllegalStateException();
                    }

                } while (previousPartitionOffset != headerPPOffset);
            }

        }

        s.init();

        return s;
    }

    private PrimerPack getPrimerPack() {
        for (Entry<KLV, MxfValue> entry : headerKLVs.entrySet()) {
            MxfValue value = entry.getValue();
            if (value instanceof PrimerPack) {
                return (PrimerPack) value;
            }
        }
        return null;
    }

    public FooterPartitionPack getFooterPartitionPack() {
        return footerPartitionPack;
    }

    public void setFooterPartitionPack(FooterPartitionPack footerPartitionPack) {
        this.footerPartitionPack = footerPartitionPack;
    }

    public int getFrameCount() {
        for (Entry<KLV, MxfValue> entry : allKLVs.entrySet()) {
            if (entry.getKey().key.equals(Sequence.Key) && ((Sequence) entry.getValue()).isPicture()) {
                return (int) ((Sequence) entry.getValue()).Duration.value.get();
            }
        }
        return -1;
    }

    public long getFrameStreamOffset(int fn) {
        for (Entry<KLV, IndexTable> entry : indexKLVs.entrySet()) {
            IndexTable idx = entry.getValue();
            if (idx.containsFrame(fn)) {
                long streamOffset = idx.getStreamOffset(fn);
                KLV closestBppKey = getHeaderKLV();
                PartitionPack closestBpp = getHeaderPartitionPack();
                long minDist = Long.MAX_VALUE;
                long bodyOffset = 0;
                for (Entry<KLV, MxfValue> entry2 : bodyKLVs.entrySet()) {
                    PartitionPack bpp = (PartitionPack) entry2.getValue();
                    if (bpp.BodySID.get() != 0) {
                        long off = bpp.BodyOffset.get();
                        long dist = streamOffset - off;
                        if (dist < minDist && off <= streamOffset) {
                            minDist = dist;
                            closestBppKey = entry2.getKey();
                            closestBpp = bpp;
                            bodyOffset = off;
                        }
                    }
                }
                long piu = closestBppKey.dataOffset + closestBppKey.len + closestBpp.HeaderByteCount.get() - bodyOffset
                        + streamOffset;
                return piu;
            }
        }
        return -1;
    }

    public static TreeMap<KLV, MxfValue> readHeader(SeekableInputStream in) throws IOException {
        KLV k0 = KLV.readKL(in);
        HeaderPartitionPack headerPartitionPack = null;
        if (HeaderPartitionPack.Key.matches(k0.key)) {
            headerPartitionPack = MxfValue.parseValue(in, k0, HeaderPartitionPack.class);
        }
        TreeMap<KLV, MxfValue> headerKLVs = new TreeMap<KLV, MxfValue>();

        LocalTagsIndex idx = null;
        if (headerPartitionPack != null && headerPartitionPack.HeaderByteCount.get() > 0) {
            headerKLVs.put(k0, headerPartitionPack);
            while (in.position() < headerPartitionPack.HeaderByteCount.get()) {
                KLV k = KLV.readKL(in);
                Class<? extends MxfValue> class1 = Registry.m.get(k.key);
                if (class1 == null) {
                    class1 = MxfValue.class;
                }
                MxfValue value;
                if (idx == null) {
                    value = MxfValue.parseValue(in, k, class1);
                } else {
                    value = MxfValue.parseValue(in, k, class1, idx);
                }
//                System.out.println(value == null ? "null" : value.getClass());
                headerKLVs.put(k, value);
                if (PrimerPack.Key.equals(k.key)) {
                    PrimerPack pp = (PrimerPack) value;
                    idx = LocalTagsIndex.createLocalTagsIndex(pp.ltks);
                }
            }
        }
        return headerKLVs;
    }

    public TimecodeComponent getTimecodeComponent() {
        for (Entry<KLV, MxfValue> entry : headerKLVs.entrySet()) {
            MxfValue value = entry.getValue();
            if (value instanceof TimecodeComponent) {
                return (TimecodeComponent) value;
            }
        }
        return null;
    }

    public TimelineTrack getTimelineTrack() {
        for (Entry<KLV, MxfValue> entry : headerKLVs.entrySet()) {
            MxfValue value = entry.getValue();
            if (value instanceof TimelineTrack) {
                return (TimelineTrack) value;
            }
        }
        return null;
    }

    public GenericPictureEssenceDescriptor getPictureEssenceDescriptor() {
        for (Entry<KLV, MxfValue> entry : headerKLVs.entrySet()) {
            MxfValue value = entry.getValue();
            if (value instanceof GenericPictureEssenceDescriptor) {
                return (GenericPictureEssenceDescriptor) value;
            }
        }
        return null;
    }

    public static void toXml(TreeMap<KLV, MxfValue> mxfStructure, File outFile) throws IOException,
            ParserConfigurationException {
        Registry registry = Registry.getInstance();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement("mxf");
        document.appendChild(root);

        for (Entry<KLV, MxfValue> entry : mxfStructure.entrySet()) {
            KLV klv = entry.getKey();
            MxfValue value = entry.getValue();

            String defaultName = "klv";
            if (Registry.FrameKey.equals(klv.key)) {
                defaultName = "frame";
            } else if (Registry.FillerKey.equals(klv.key)) {
                defaultName = "filler";
            }
            Element element = document.createElement(value == null ? defaultName : value.getClass().getName());
            element.setAttribute("offset", "" + klv.offset);
            element.setAttribute("key", klv.key.toString());
            element.setAttribute("len", "" + klv.len);
            Registry.ULDesc ulDesc = registry.get(klv.key);
            if (ulDesc != null) {
                element.setAttribute("name", ulDesc.refName);
                Element desc = document.createElement("desc");
                desc.setTextContent(ulDesc.desc);
                element.appendChild(desc);
            }
            if (value != null) {
                value.toXml(document, element);
            }
            root.appendChild(element);
        }

        NodeList elst = document.getElementsByTagName("key");
        for (int i = 0; i < elst.getLength(); i++) {
            Element item = (Element) elst.item(i);
            if (item.hasAttribute("ul")) {
                String ul = item.getAttribute("ul");
                Registry.ULDesc ulDesc = registry.get(ul);
                if (ulDesc != null) {
                    item.setAttribute("name", ulDesc.refName);
                    item.setAttribute("desc", ulDesc.desc);
                }
            }
        }

        XmlUtil.writeXml(document, outFile);
    }

    public Document toXml() throws IOException, ParserConfigurationException {
        Registry registry = Registry.getInstance();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement("mxf");
        document.appendChild(root);

        for (Entry<KLV, MxfValue> entry : allKLVs.entrySet()) {
            KLV klv = entry.getKey();
            MxfValue value = entry.getValue();

            String defaultName = "klv";
            if (Registry.FrameKey.equals(klv.key)) {
                defaultName = "frame";
            } else if (Registry.FillerKey.equals(klv.key)) {
                defaultName = "filler";
            }
            Element element = document.createElement(value == null ? defaultName : value.getClass().getName());
            element.setAttribute("offset", "" + klv.offset);
            element.setAttribute("key", klv.key.toString());
            element.setAttribute("len", "" + klv.len);
            Registry.ULDesc ulDesc = registry.get(klv.key);
            if (ulDesc != null) {
                element.setAttribute("name", ulDesc.refName);
                Element desc = document.createElement("desc");
                desc.setTextContent(ulDesc.desc);
                element.appendChild(desc);
            }
            if (value != null) {
                value.toXml(document, element);
            }
            root.appendChild(element);
        }

        NodeList elst = document.getElementsByTagName("key");
        for (int i = 0; i < elst.getLength(); i++) {
            Element item = (Element) elst.item(i);
            if (item.hasAttribute("ul")) {
                String ul = item.getAttribute("ul");
                Registry.ULDesc ulDesc = registry.get(ul);
                if (ulDesc != null) {
                    item.setAttribute("name", ulDesc.refName);
                    item.setAttribute("desc", ulDesc.desc);
                }
            }
        }

        return document;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("usage: " + MxfStructure.class.getName() + " in.mxf {out.xml}");
            System.exit(1);
        }

        File f = new File(args[0]);
        OutputStream out = System.out;
        if (args.length > 1) {
            out = new BufferedOutputStream(new FileOutputStream(new File(args[1])));
        }

        SeekableFileInputStream in = new SeekableFileInputStream(f);
        MxfStructure structure = MxfStructure.readStructure(in);
        Document xml = structure.toXml();
        XmlUtil.writeXml(xml, out);
        in.close();
        out.close();
    }

    public WaveAudioEssenceDescriptor getWaveAudio() {
        Set<Entry<KLV, MxfValue>> entrySet = headerKLVs.entrySet();
        for (Entry<KLV, MxfValue> entry : entrySet) {
            if (WaveAudioEssenceDescriptor.Key.equals(entry.getKey().key)) {
                return (WaveAudioEssenceDescriptor) entry.getValue();
            }
        }
        return null;
    }

}