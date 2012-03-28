package com.vg.mxf;

import static com.vg.util.BER.encodeLength;
import static com.vg.util.FileUtil.readFullyOrDie;
import static com.vg.util.FileUtil.writeFully;
import static org.junit.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
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
import org.apache.commons.io.output.CountingOutputStream;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vg.util.BER;
import com.vg.util.FileUtil;
import com.vg.util.LongArrayList;
import com.vg.util.SeekableFileInputStream;
import com.vg.util.SeekableInputStream;

@Ignore
public class MxfTest {

    @Test
    public void testDecode() throws Exception {
        byte b[] = new byte[] { 0, 0, 0, 1, 0, 0, 0, 0x10 };
        long decodeLength = BER.decodeLength(new ByteArrayInputStream(b));
        System.out.println(decodeLength);
        System.out.println((int) '0');
    }

    @Test
    public void testToXml2() throws Exception {
        SeekableInputStream in = j2k();
        TreeMap<KLV, MxfValue> mxfStructure = new TreeMap<KLV, MxfValue>();

        while (in.position() < in.length()) {
            //            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset));
            KLV kl = KLV.readKL(in);
            Class<? extends MxfValue> class1 = Registry.m.get(kl.key);
            if (class1 == null) {
                mxfStructure.put(kl, null);
                Assert.assertEquals(kl.len, in.skip(kl.len));
            } else {
                MxfValue value = MxfValue.parseValue(in, kl, class1);
                mxfStructure.put(kl, value);
            }
        }
        in.close();

        toXml(mxfStructure, new File("tmp/out.xml"));
    }

    @Test
    public void testTrim() throws Exception {
        File file = new File("tmp/spiderman_.mxf");
        RandomAccessFile r = new RandomAccessFile(file, "rw");
        r.setLength(3272093809L);
        r.close();
    }

    @Test
    public void testConsistency() throws Exception {
        SeekableInputStream in = new SeekableFileInputStream(new File("tmp/header2.mxf"));
        MxfStructure s = MxfStructure.readStructure(in);
        for (Entry<KLV, MxfValue> entry : s.entries()) {
            MxfValue value = entry.getValue();
            System.out.println(value.getClass() + " " + value.getInstanceUID().toString().substring(0, 4));
            for (UUID uuid : value.getReferencedUIDs()) {
                MxfValue referenced = s.getValue(uuid);
                System.out.println("    ref: " + (referenced != null ? referenced.getClass() : "null") + " "
                        + uuid.toString().substring(0, 4));
            }

        }
    }

    @Test
    public void testToXml3() throws Exception {
        SeekableInputStream in = new SeekableFileInputStream(new File("testdata/DawnOfTheDead_TRAILER3_110711_01.mxf"));
        TreeMap<KLV, MxfValue> mxfStructure = new TreeMap<KLV, MxfValue>();

        while (in.position() < in.length()) {
            long offset = in.position();
            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset));
            KLV kl = KLV.readKL(in);
            Class<? extends MxfValue> class1 = Registry.m.get(kl.key);
            System.out.println(class1);
            if (class1 == null) {
                mxfStructure.put(kl, null);
                Assert.assertEquals(kl.len, in.skip(kl.len));
            } else {
                MxfValue value = MxfValue.parseValue(in, kl, class1);
                mxfStructure.put(kl, value);
            }
        }
        in.close();

        toXml(mxfStructure, new File("tmp/out3.xml"));
    }

    @Test
    public void testToXmlSpiderman() throws Exception {
        SeekableInputStream in = ntsc();
        TreeMap<KLV, MxfValue> mxfStructure = new TreeMap<KLV, MxfValue>();

        while (in.position() < in.length()) {
            //            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset));
            KLV kl = KLV.readKL(in);
            Class<? extends MxfValue> class1 = Registry.m.get(kl.key);
            if (class1 == null) {
                if (!Registry.FrameKey.equals(kl.key)) {
                    mxfStructure.put(kl, null);
                }
                Assert.assertEquals(kl.len, in.skip(kl.len));
            } else {
                MxfValue value = MxfValue.parseValue(in, kl, class1);
                mxfStructure.put(kl, value);
            }
        }
        in.close();

        toXml(mxfStructure, new File("tmp/wof.xml"));
    }

    @Test
    public void testWrite() throws Exception {
        SeekableInputStream in = j2k();
        TreeMap<KLV, MxfValue> header = MxfStructure.readHeader(in);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("tmp/header.mxf")));

        for (Entry<KLV, MxfValue> entry : header.entrySet()) {
            KLV k = entry.getKey();
            MxfValue v = entry.getValue();
            int capacity = v.getByteBuffer().capacity();
            Assert.assertEquals(k.len, capacity);
            out.write(k.key.getBytes());
            BER.encodeLength(out, k.len, k.getLenByteCount() - 1);
            FileUtil.writeFully(v.getByteBuffer(), out);
        }
        out.close();
        in.close();
    }

    @Test
    public void testWrite2() throws Exception {
        SeekableInputStream in = j2k();
        MxfStructure s = MxfStructure.readStructure(in);
        TreeMap<KLV, MxfValue> header = s.headerKLVs;

        //hack header
        for (Entry<KLV, MxfValue> entry : header.entrySet()) {
            MxfValue v = entry.getValue();
            if (v != null && v instanceof StructuralComponent) {
                StructuralComponent sc = (StructuralComponent) v;
                sc.setDuration(46);
            }
        }
        s.getEssenceContainerData().IndexSID.value.set(0);

        File outMxf = new File("tmp/header2.mxf");
        CountingOutputStream out = new CountingOutputStream(new BufferedOutputStream(new FileOutputStream(outMxf)));

        for (Entry<KLV, MxfValue> entry : header.entrySet()) {
            KLV k = entry.getKey();
            MxfValue v = entry.getValue();
            int capacity = v.getByteBuffer().capacity();
            assertEquals(k.len, capacity);
            out.write(k.key.getBytes());
            encodeLength(out, k.len, k.getLenByteCount() - 1);
            FileUtil.writeFully(v.getByteBuffer().duplicate(), out);
        }

        long bodyOffset = s.getBodyOffset();
        FileUtil.forceSeek(in, bodyOffset);
        int fn = 0;
        do {
            KLV k = KLV.readKL(in);
            if (Registry.FrameKey.equals(k.key)) {
                ByteBuffer frameBuf = ByteBuffer.allocate((int) k.len);
                readFullyOrDie(in, frameBuf).flip();
                out.write(k.key.getBytes());
                encodeLength(out, k.len, k.getLenByteCount() - 1);
                FileUtil.writeFully(frameBuf, out);
                fn++;
            }

        } while (fn != 46 && in.position() < in.length());
        in.close();

        //        long bppPos = out.getByteCount();
        //        BodyPartitionPack bpp = s.getBodyPartitionPack(1);
        //        bpp.BodyOffset.set(bodyOffset+20);
        //        bpp.IndexSID.set(0);
        //        bpp.PreviousPartition.set(0);
        //        bpp.ThisPartition.set(bppPos);
        //        bpp.FooterPartition.set(bppPos + 16 + 4 + bpp.size());
        //        assertEquals(0L, bpp.HeaderByteCount.get());
        //        out.write(BodyPartitionPack.Key.getBytes());
        //        encodeLength(out, bpp.size(), 3);
        //        FileUtil.writeFully(bpp.getByteBuffer(), out);

        long fppPos = out.getByteCount();
        FooterPartitionPack fpp = s.getFooterPartitionPack();
        fpp.PreviousPartition.set(0);
        fpp.ThisPartition.set(fppPos);
        fpp.FooterPartition.set(fppPos);
        out.write(FooterPartitionPack.Key.getBytes());
        encodeLength(out, fpp.size(), 3);
        FileUtil.writeFully(fpp.getByteBuffer().duplicate(), out);

        //rewrite header
        out.close();
        RandomAccessFile raf = new RandomAccessFile(outMxf, "rw");
        FileChannel channel = raf.getChannel();

        HeaderPartitionPack hpp = s.getHeaderPartitionPack();
        hpp.FooterPartition.set(fppPos);
        ByteArrayOutputStream bout = new ByteArrayOutputStream(20);
        bout.write(HeaderPartitionPack.Key.getBytes());
        encodeLength(bout, hpp.size(), 3);
        writeFully(ByteBuffer.wrap(bout.toByteArray()), channel);
        writeFully(hpp.getByteBuffer().duplicate(), channel);
        channel.close();
        raf.close();

    }

    @Test
    public void testStructure() throws Exception {
        MxfStructure.readStructure(new SeekableFileInputStream(new File("/Volumes/testdata/textless.mxf")));
    }

    @Test
    public void testReadAllKlvs() throws Exception {
        SeekableFileInputStream in = new SeekableFileInputStream(new File("/Volumes/testdata/textless.mxf"));
        long length = in.length();
        while (in.position() < length) {
            KLV kl = KLV.readKL(in);
            System.out.println(kl.key.toString() + " " + kl.offset + " " + kl.len);
            in.skip(kl.len);
        }
        in.close();

    }

    @Test
    public void testIndex() throws Exception {
        //read header
        SeekableInputStream in = j2k();
        TreeMap<KLV, MxfValue> headerKLVs = MxfStructure.readHeader(in);

        HeaderPartitionPack headerPartitionPack = (HeaderPartitionPack) headerKLVs.firstEntry();
        if (headerPartitionPack != null && headerPartitionPack.HeaderByteCount.get() > 0) {
            //read footer
            long footerOffset = headerPartitionPack.FooterPartition.get();
            Assert.assertTrue(footerOffset > 0);
            in.seek(footerOffset);
            KLV kl = KLV.readKL(in);
            Assert.assertEquals(FooterPartitionPack.Key, kl.key);
            FooterPartitionPack footerPartitionPack = MxfValue.parseValue(in, kl, FooterPartitionPack.class);
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
                BodyPartitionPack bpp = MxfValue.parseValue(in, kl, BodyPartitionPack.class);
                System.out.println(bpp.toDebugString());
                //sanity check: make sure partition offsets are actually going backwards
                Assert.assertTrue(bpp.PreviousPartition.get() < previousPartitionOffset);
                bodyKLVs.put(kl, bpp);
                previousPartitionOffset = bpp.PreviousPartition.get();
                if (bpp.IndexByteCount.get() > 0 && bpp.IndexSID.get() > 0) {
                    //index follows
                    kl = KLV.readKL(in);
                    Assert.assertEquals(IndexTable.Key, kl.key);
                    IndexTable indexTable = MxfValue.parseValue(in, kl, IndexTable.class);
                    indexKLVs.put(kl, indexTable);
                }

            } while (previousPartitionOffset != headerPPOffset);

        }

    }

    private SeekableInputStream j2k() throws IOException {
        File mxf = FileUtil.tildeExpand("~/testdata/j2k/hd/stem/media/StemTape_2010_HD_16x9_240_2398_english_0630_JPEG2000_v0.mxf");
        SeekableInputStream in = new SeekableFileInputStream(mxf);
        return in;
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

        TagValue v = new TagValue(0, 32);
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

    @Test
    public void testWrite3() throws Exception {
        SeekableInputStream in = ntsc();
        OutputStream out = new BufferedOutputStream(new FileOutputStream("tmp/wof2.mxf"));

        in.seek(1224733702);
        KLV k0 = KLV.readKL(in);

        int frameCount = 0;

        ByteBuffer blackFrame = null;

        while (in.position() < in.length()) {
            long offset = in.position();
            KLV kl = KLV.readKL(in);
            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset) + " size: " + kl.len);
            out.write(kl.key.getBytes());
            encodeLength(out, kl.len, kl.getLenByteCount() - 1);
            ByteBuffer buf = ByteBuffer.allocate((int) kl.len);
            boolean isFrame = Registry.FrameKey.equals(kl.key);
            boolean smallFrame = isFrame && blackFrame != null && blackFrame.capacity() > kl.len;
            if (smallFrame) {
                System.out.println("small frame");
            }
            if (!isFrame || (frameCount++) < 3240 || smallFrame) {
                FileUtil.readFullyOrDie(in, buf).flip();
            } else {
                if (blackFrame != null) {
                    blackFrame.clear();
                    buf.put(blackFrame);
                    buf.clear();
                }
                in.skip(kl.len);
            }
            if (frameCount == 3240) {
                blackFrame = ByteBuffer.allocate((int) kl.len);
                buf.clear();
                blackFrame.put(buf);
                buf.clear();
                blackFrame.clear();
            }
            FileUtil.writeFully(buf, out);
        }
        in.close();
        out.close();

    }

    @Test
    public void testSmallestFrame() throws Exception {
        SeekableInputStream in = ntsc();

        int min = Integer.MAX_VALUE;
        long minoffset = 0;

        while (in.position() < in.length()) {
            long offset = in.position();
            KLV kl = KLV.readKL(in);
            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset) + " size: " + kl.len);
            if (Registry.FrameKey.equals(kl.key)) {
                if (kl.len < min) {
                    min = (int) kl.len;
                    minoffset = offset;
                }
            }
            in.skip(kl.len);
        }
        System.out.println("min frame: " + min + " offset: " + minoffset);
        in.close();

    }

    @Test
    public void testNTSC() throws Exception {
        SeekableInputStream in = ntsc();
        OutputStream out = new BufferedOutputStream(new FileOutputStream("tmp/wof2.mxf"));

        in.seek(1224733702);
        KLV k0 = KLV.readKL(in);
        ByteBuffer blackFrame = ByteBuffer.allocate((int) k0.len);
        FileUtil.readFullyOrDie(in, blackFrame).flip();
        in.seek(0);

        int frameCount = 0;

        while (in.position() < in.length()) {
            long offset = in.position();
            KLV kl = KLV.readKL(in);
            System.out.println("pos in file " + offset + " 0x" + Long.toHexString(offset) + " size: " + kl.len);
            out.write(kl.key.getBytes());
            encodeLength(out, kl.len, kl.getLenByteCount() - 1);
            ByteBuffer buf = ByteBuffer.allocate((int) kl.len);
            boolean isFrame = Registry.FrameKey.equals(kl.key);
            boolean smallFrame = isFrame && blackFrame != null && blackFrame.capacity() > kl.len;
            if (smallFrame) {
                System.out.println("small frame");
            }
            if (!isFrame || (frameCount++) < 46 || smallFrame) {
                FileUtil.readFullyOrDie(in, buf).flip();
            } else {
                if (blackFrame != null) {
                    blackFrame.clear();
                    buf.put(blackFrame);
                    buf.clear();
                }
                in.skip(kl.len);
            }
            FileUtil.writeFully(buf, out);
        }
        in.close();
        out.close();

    }

    private SeekableInputStream ntsc() throws IOException {
        SeekableInputStream in = new SeekableFileInputStream(new File("tmp/wof.mxf"));
        return in;
    }

    @Test
    public void testStreamOffset() throws Exception {
        SeekableInputStream in = j2k();
        MxfStructure s = MxfStructure.readStructure(in);
        in.close();
        Assert.assertEquals(12210, s.getFrameStreamOffset(0));
        Assert.assertEquals(131598, s.getFrameStreamOffset(1));
        Assert.assertEquals(220386932, s.getFrameStreamOffset(240));
        Assert.assertEquals(533793240, s.getFrameStreamOffset(481));
    }

    @Test
    public void testSingleBodyPartitionSingleIndex() throws Exception {
        SeekableInputStream in = j2k();
        MxfStructure structure = MxfStructure.readStructure(in);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("tmp/index.mxf")));
        long length = in.length();
        in.seek(0);
        while (in.position() < length) {
            KLV kl = KLV.readKL(in);
            if (IndexTable.Key.equals(kl.key) || BodyPartitionPack.Key.equals(kl.key) || RandomIndex.Key.equals(kl.key)) {
                in.skip(kl.len);
            } else {
                out.write(kl.key.getBytes());
                encodeLength(out, kl.len, kl.getLenByteCount() - 1);
                ByteBuffer buf = ByteBuffer.allocate((int) kl.len);
                FileUtil.readFullyOrDie(in, buf).clear();
                FileUtil.writeFully(buf, out);
            }

        }
    }

    @Test
    public void testSimpleIndex() throws Exception {
        SeekableInputStream in = new SeekableFileInputStream(new File("tmp/header2.mxf"));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("tmp/index2.mxf")));
        long length = in.length();
        in.seek(0);
        LongArrayList offsets = new LongArrayList();
        int frameCount = 0;
        long firstFrameOffset = -1;
        while (in.position() < length) {
            KLV kl = KLV.readKL(in);
            if (FooterPartitionPack.Key.equals(kl.key)) {
                FooterPartitionPack footer = MxfValue.parseValue(in, kl, FooterPartitionPack.class);
                IndexTable index = IndexTable.createTable(offsets.toNativeArray());
                index.InstanceUID.uuid.hi.set(0x0001020304050607L);
                index.InstanceUID.uuid.lo.set(0x08090a0b0c0d0e0fL);
                index.IndexEditRate.num.set(24000);
                index.IndexEditRate.den.set(1001);
                index.IndexStartPosition.value.set(0);
                index.IndexDuration.value.set(frameCount);
                index.IndexSID.value.set(129);
                index.BodySID.value.set(1);

                footer.IndexSID.set(129);
                footer.BodySID.set(1);
                footer.IndexByteCount.set(index.size());

                out.write(FooterPartitionPack.Key.getBytes());
                encodeLength(out, kl.len, 3);
                footer.write(out);

                out.write(IndexTable.Key.getBytes());
                encodeLength(out, index.size(), 3);
                index.write(out);
                break;
            } else if (IndexTable.Key.equals(kl.key) || BodyPartitionPack.Key.equals(kl.key)
                    || RandomIndex.Key.equals(kl.key)) {
                in.skip(kl.len);
            } else {
                out.write(kl.key.getBytes());
                encodeLength(out, kl.len, kl.getLenByteCount() - 1);
                ByteBuffer buf = ByteBuffer.allocate((int) kl.len);
                FileUtil.readFullyOrDie(in, buf).clear();
                FileUtil.writeFully(buf, out);
            }
            if (Registry.FrameKey.equals(kl.key)) {
                if (firstFrameOffset == -1) {
                    firstFrameOffset = kl.offset;
                }
                long off = kl.offset - firstFrameOffset;
                offsets.add(off);
                frameCount++;
            }

        }

        in.close();
        out.close();

    }

    @Test
    public void testAnnotations() throws Exception {
        Annotation[] annotations = this.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }

        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            System.out.println(method);
            for (Annotation annotation : method.getAnnotations()) {
                System.out.println("\t" + annotation);
            }

        }

    }

    @Test
    public void testNtsc2() throws Exception {
        SeekableInputStream in = ntsc();
        MxfStructure s = MxfStructure.readStructure(in);
        TreeMap<KLV, MxfValue> header = s.headerKLVs;

        //hack header
        for (Entry<KLV, MxfValue> entry : header.entrySet()) {
            MxfValue v = entry.getValue();
            if (v != null && v instanceof StructuralComponent) {
                StructuralComponent sc = (StructuralComponent) v;
                sc.setDuration(46);
            }
        }
        s.getEssenceContainerData().IndexSID.value.set(0);

        File outMxf = new File("tmp/ntsc2.mxf");
        CountingOutputStream out = new CountingOutputStream(new BufferedOutputStream(new FileOutputStream(outMxf)));

        for (Entry<KLV, MxfValue> entry : header.entrySet()) {
            KLV k = entry.getKey();
            MxfValue v = entry.getValue();
            int capacity = v.getByteBuffer().capacity();
            assertEquals(k.len, capacity);
            out.write(k.key.getBytes());
            encodeLength(out, k.len, k.getLenByteCount() - 1);
            FileUtil.writeFully(v.getByteBuffer().duplicate(), out);
        }

        long bodyOffset = s.getBodyOffset();
        FileUtil.forceSeek(in, bodyOffset);
        int fn = 0;
        do {
            KLV k = KLV.readKL(in);
            if (Registry.FrameKey.equals(k.key)) {
                ByteBuffer frameBuf = ByteBuffer.allocate((int) k.len);
                readFullyOrDie(in, frameBuf).flip();
                out.write(k.key.getBytes());
                encodeLength(out, k.len, k.getLenByteCount() - 1);
                FileUtil.writeFully(frameBuf, out);
                fn++;
            }

        } while (fn != 46 && in.position() < in.length());
        in.close();

        long fppPos = out.getByteCount();
        FooterPartitionPack fpp = s.getFooterPartitionPack();
        fpp.PreviousPartition.set(0);
        fpp.ThisPartition.set(fppPos);
        fpp.FooterPartition.set(fppPos);
        out.write(FooterPartitionPack.Key.getBytes());
        encodeLength(out, fpp.size(), 3);
        FileUtil.writeFully(fpp.getByteBuffer().duplicate(), out);

        //rewrite header
        out.close();
        RandomAccessFile raf = new RandomAccessFile(outMxf, "rw");
        FileChannel channel = raf.getChannel();

        HeaderPartitionPack hpp = s.getHeaderPartitionPack();
        hpp.FooterPartition.set(fppPos);
        ByteArrayOutputStream bout = new ByteArrayOutputStream(20);
        bout.write(HeaderPartitionPack.Key.getBytes());
        encodeLength(bout, hpp.size(), 3);
        writeFully(ByteBuffer.wrap(bout.toByteArray()), channel);
        writeFully(hpp.getByteBuffer().duplicate(), channel);
        channel.close();
        raf.close();

    }
}
