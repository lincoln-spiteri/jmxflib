package com.vg.mxf;

import static com.vg.mxf.Key.key;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.input.CountingInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vg.util.BER;
import com.vg.util.FileUtil;
import com.vg.util.RandomAccessFileBufferedInputStream;
import com.vg.util.SeekableInputStream;

public class MxfTest {

    @Test
    public void testDecode() throws Exception {
        byte b[] = new byte[] { 0, 0, 0, 1, 0, 0, 0, 0x10 };
        long decodeLength = BER.decodeLength(new ByteArrayInputStream(b));
        System.out.println(decodeLength);
        System.out.println((int) '0');
    }

    public static class LocalTags {
        TreeMap<Integer, LocalTagKey> tag2key;
        TreeMap<String, LocalTagKey> key2key;
        TreeMap<String, Registry.ULDesc> map;

        public LocalTagKey getLtk(String key) {
            return key2key.get(key);
        }

        public LocalTagKey getLtk(int tag) {
            return tag2key.get(tag);
        }

        public Registry.ULDesc getDesc(String key) {
            return map.get(key);
        }
    }

    static TreeMap<Key, Class<? extends MxfValue>> m = new TreeMap<Key, Class<? extends MxfValue>>();
    static {
        m.put(HeaderPartitionPack.Key, HeaderPartitionPack.class);
        m.put(BodyPartitionPack.Key, BodyPartitionPack.class);
        m.put(FooterPartitionPack.Key, FooterPartitionPack.class);
        m.put(IndexTable.Key, IndexTable.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.2F.00"), Preface.class);
        m.put(key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.05.01.00"), PrimerPack.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.30.00"), Identification.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.18.00"), ContentStorage.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.23.00"), EssenceContainerData.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.36.00"), MaterialPackage.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.3B.00"), TimelineTrack.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.0F.00"), Sequence.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.14.00"), TimecodeComponent.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.11.00"), SourceClip.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.37.00"), SourcePackage.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.28.00"), CDCIEssenceDescriptor.class);
        m.put(key("06.0E.2B.34.02.53.01.01.0D.01.01.01.01.01.5A.00"), JPEG2000PictureSubDescriptor.class);
        m.put(key("06.0E.2B.34.02.05.01.01.0D.01.02.01.01.11.01.00"), RandomIndex.class);

    }

    @Test
    public void testReadKL() throws Exception {
        Registry map = Registry.getInstance();
        File mxf = FileUtil.tildeExpand("~/testdata/j2k/hd/stem/media/StemTape_2010_HD_16x9_240_2398_english_0630_JPEG2000_v0.mxf");
        CountingInputStream in = new CountingInputStream(new FileInputStream(mxf));
        TreeMap<KLV, MxfValue> mxfStructure = new TreeMap<KLV, MxfValue>();
        Key frameKey = key("06.0E.2B.34.01.02.01.01.0D.01.03.01.15.01.08.01");

        while (in.getByteCount() < mxf.length()) {
            long offset = in.getByteCount();
            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset));
            KLV kl = KLV.readKL(in);
            kl.offset = offset;
            Registry.ULDesc ulDesc = map.get(kl.key);
            System.out.println(kl);
            System.out.println(ulDesc);
            Class<? extends MxfValue> class1 = m.get(kl.key);
            System.out.println(class1);
            if (class1 == null || !frameKey.equals(kl.key)) {
                mxfStructure.put(kl, null);
                Assert.assertEquals(kl.len, in.skip(kl.len));
            } else {
                ByteBuffer buf = ByteBuffer.allocate((int) kl.len);
                FileUtil.readFullyOrDie(in, buf).flip();
                MxfValue value = class1.newInstance();
                value.setByteBuffer(buf, 0);
                value.parse();
                mxfStructure.put(kl, value);
            }
        }
        in.close();

    }

    @Test
    public void testXml() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element element = document.createElement("asd");
        document.appendChild(element);
        Element element2 = document.createElement("asd");
        document.appendChild(element2);

    }

    @Test
    public void testToXml() throws Exception {
        Registry map = Registry.getInstance();
        File mxf = FileUtil.tildeExpand("~/testdata/j2k/hd/stem/media/StemTape_2010_HD_16x9_240_2398_english_0630_JPEG2000_v0.mxf");
        CountingInputStream in = new CountingInputStream(new FileInputStream(mxf));
        TreeMap<KLV, MxfValue> mxfStructure = new TreeMap<KLV, MxfValue>();

        while (in.getByteCount() < mxf.length()) {
            long offset = in.getByteCount();
            //            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset));
            KLV kl = KLV.readKL(in);
            kl.offset = offset;
            Class<? extends MxfValue> class1 = m.get(kl.key);
            if (class1 == null) {
                System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset));
                //                if (!frameKey.equals(kl.key)) {
                mxfStructure.put(kl, null);
                //                } else {
                //                    break;
                //                }
                Assert.assertEquals(kl.len, in.skip(kl.len));
            } else {
                System.out.println(class1);
                ByteBuffer buf = ByteBuffer.allocate((int) kl.len);
                FileUtil.readFullyOrDie(in, buf).flip();
                MxfValue value = class1.newInstance();
                value.setByteBuffer(buf, 0);
                value.parse();
                mxfStructure.put(kl, value);
            }
        }
        in.close();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement("mxf");
        document.appendChild(root);

        int J = 0;
        for (Entry<KLV, MxfValue> entry : mxfStructure.entrySet()) {
            KLV klv = entry.getKey();
            MxfValue value = entry.getValue();

            String defaultName = "klv";
            if (frameKey.equals(klv.key)) {
                defaultName = "frame";
            } else if (fillerKey.equals(klv.key)) {
                defaultName = "filler";
            }
            Element element = document.createElement(value == null ? defaultName : value.getClass().getName());
            element.setAttribute("offset", "" + klv.offset);
            element.setAttribute("key", klv.key.toString());
            element.setAttribute("len", "" + klv.len);
            Registry.ULDesc ulDesc = map.get(klv.key);
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
            J++;
            System.out.println(mxfStructure.size() + " " + J);
            //            if (J > 14) {
            //                break;
            //            }
        }

        NodeList elst = document.getElementsByTagName("key");
        for (int i = 0; i < elst.getLength(); i++) {
            Element item = (Element) elst.item(i);
            if (item.hasAttribute("ul")) {
                String ul = item.getAttribute("ul");
                Registry.ULDesc ulDesc = map.get(ul);
                if (ulDesc != null) {
                    item.setAttribute("name", ulDesc.refName);
                    item.setAttribute("desc", ulDesc.desc);
                }
            }
        }

        File outxml = new File("tmp/out.xml");
        writeXml(document, outxml);
        System.out.println("done");

    }

    public final static Key frameKey = key("06.0E.2B.34.01.02.01.01.0D.01.03.01.15.01.08.01");
    public final static Key fillerKey = key("06.0E.2B.34.01.01.01.02.03.01.02.10.01.00.00.00");

    @Test
    public void testToXml2() throws Exception {
        File mxf = FileUtil.tildeExpand("~/testdata/j2k/hd/stem/media/StemTape_2010_HD_16x9_240_2398_english_0630_JPEG2000_v0.mxf");
        SeekableInputStream in = new RandomAccessFileBufferedInputStream(mxf);
        TreeMap<KLV, MxfValue> mxfStructure = new TreeMap<KLV, MxfValue>();

        while (in.position() < mxf.length()) {
            //            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset));
            KLV kl = KLV.readKL(in);
            Class<? extends MxfValue> class1 = m.get(kl.key);
            if (class1 == null) {
                mxfStructure.put(kl, null);
                Assert.assertEquals(kl.len, in.skip(kl.len));
            } else {
                MxfValue value = parseValue(in, kl, class1);
                mxfStructure.put(kl, value);
            }
        }
        in.close();

        toXml(mxfStructure, new File("tmp/out.xml"));
    }

    private static <T extends MxfValue> T parseValue(SeekableInputStream in, KLV kl, Class<T> class1)
            throws IOException, InstantiationException, IllegalAccessException {
        ByteBuffer buf = ByteBuffer.allocate((int) kl.len);
        FileUtil.readFullyOrDie(in, buf).flip();
        T value = class1.newInstance();
        value.setByteBuffer(buf, 0);
        value.parse();
        return value;
    }

    @Test
    public void testIndex() throws Exception {
        //read header
        File mxf = FileUtil.tildeExpand("~/testdata/j2k/hd/stem/media/StemTape_2010_HD_16x9_240_2398_english_0630_JPEG2000_v0.mxf");
        SeekableInputStream in = new RandomAccessFileBufferedInputStream(mxf);
        KLV kl = KLV.readKL(in);
        HeaderPartitionPack headerPartitionPack = null;
        if (HeaderPartitionPack.Key.equals(kl.key)) {
            headerPartitionPack = parseValue(in, kl, HeaderPartitionPack.class);
            System.out.println(headerPartitionPack.toDebugString());
        }
        TreeMap<KLV, MxfValue> headerKLVs = new TreeMap<KLV, MxfValue>();

        if (headerPartitionPack != null && headerPartitionPack.HeaderByteCount.get() > 0) {
            while (in.position() < headerPartitionPack.HeaderByteCount.get()) {
                kl = KLV.readKL(in);
                System.out.println("pos in file " + kl.offset + " 0x" + Long.toHexString(kl.offset));
                Class<? extends MxfValue> class1 = m.get(kl.key);
                if (class1 == null) {
                    Assert.assertEquals(kl.len, in.skip(kl.len));
                } else {
                    MxfValue value = parseValue(in, kl, class1);
                    headerKLVs.put(kl, value);
                }
            }

            //read footer
            long footerOffset = headerPartitionPack.FooterPartition.get();
            Assert.assertTrue(footerOffset > 0);
            in.seek(footerOffset);
            kl = KLV.readKL(in);
            Assert.assertEquals(FooterPartitionPack.Key, kl.key);
            FooterPartitionPack footerPartitionPack = parseValue(in, kl, FooterPartitionPack.class);
            long headerPPOffset = headerPartitionPack.ThisPartition.get();
            long previousPartitionOffset = footerPartitionPack.PreviousPartition.get();

            //read backwards partitions and indexes
            TreeMap<KLV, MxfValue> bodyKLVs = new TreeMap<KLV, MxfValue>();
            TreeMap<KLV, MxfValue> indexKLVs = new TreeMap<KLV, MxfValue>();
            do {
                in.seek(previousPartitionOffset);
                kl = KLV.readKL(in);
                System.out.println("pos in file " + kl.offset + " 0x" + Long.toHexString(kl.offset));
                Assert.assertEquals(BodyPartitionPack.Key, kl.key);
                BodyPartitionPack bpp = parseValue(in, kl, BodyPartitionPack.class);
                System.out.println(bpp.toDebugString());
                //sanity check: make sure partition offsets are actually going backwards
                Assert.assertTrue(bpp.PreviousPartition.get() < previousPartitionOffset);
                bodyKLVs.put(kl, bpp);
                previousPartitionOffset = bpp.PreviousPartition.get();
                if (bpp.IndexByteCount.get() > 0 && bpp.IndexSID.get() > 0) {
                    //index follows
                    kl = KLV.readKL(in);
                    Assert.assertEquals(IndexTable.Key, kl.key);
                    IndexTable indexTable = parseValue(in, kl, IndexTable.class);
                    indexKLVs.put(kl, indexTable);
                }

            } while (previousPartitionOffset != headerPPOffset);

        }
        for (Entry<KLV, MxfValue> entry : headerKLVs.entrySet()) {
            int lenByteCount = entry.getKey().getLenByteCount();
            System.out.println(lenByteCount);
        }

    }

    private void toXml(TreeMap<KLV, MxfValue> mxfStructure, File outFile) throws IOException,
            ParserConfigurationException {
        Registry registry = Registry.getInstance();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement("mxf");
        document.appendChild(root);

        int J = 0;
        for (Entry<KLV, MxfValue> entry : mxfStructure.entrySet()) {
            KLV klv = entry.getKey();
            MxfValue value = entry.getValue();

            String defaultName = "klv";
            if (frameKey.equals(klv.key)) {
                defaultName = "frame";
            } else if (fillerKey.equals(klv.key)) {
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
            J++;
            System.out.println(mxfStructure.size() + " " + J);
            //            if (J > 14) {
            //                break;
            //            }
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

        writeXml(document, outFile);
        System.out.println("done");
    }

    @Test
    public void testTagValueXml() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("mxf");
        doc.appendChild(root);

        TagValue v = new TagValue(32);
        v.setByteBuffer(ByteBuffer.allocate(32 + 4), 0);
        Element xml = doc.createElement("v");
        Element xml2 = v.toXml(doc, xml);
        root.appendChild(xml2);
        writeXml(doc, new File("tmp/out.xml"));
    }

    public static void writeXml(Document document, File outxml) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer;
            serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(outxml);
            serializer.transform(domSource, streamResult);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

}
