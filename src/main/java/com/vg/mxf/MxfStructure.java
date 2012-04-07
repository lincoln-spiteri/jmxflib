package com.vg.mxf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.vg.util.SeekableInputStream;

public class MxfStructure {
    private HeaderPartitionPack headerPartitionPack;
    private KLV footerKLV;
    private FooterPartitionPack footerPartitionPack;
    public TreeMap<KLV, MxfValue> headerKLVs = new TreeMap<KLV, MxfValue>();
    public TreeMap<KLV, MxfValue> bodyKLVs = new TreeMap<KLV, MxfValue>();
    public TreeMap<KLV, MxfValue> indexKLVs = new TreeMap<KLV, MxfValue>();
    public TreeMap<KLV, MxfValue> allKLVs = new TreeMap<KLV, MxfValue>();
    public TreeMap<UUID, MxfValue> uuidIndex = new TreeMap<UUID, MxfValue>();

    long getBodyOffset() {
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
        allKLVs.put(footerKLV, getFooterPartitionPack());
        for (Entry<KLV, MxfValue> entry : allKLVs.entrySet()) {
            MxfValue value = entry.getValue();
            UUID instanceUID = value.getInstanceUID();
            uuidIndex.put(instanceUID, value);
        }

        //System.out.println("index size: " + uuidIndex.size());
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
            assertTrue(footerOffset > 0);
            in.seek(footerOffset);
            KLV kl = KLV.readKL(in);
            assertEquals(FooterPartitionPack.Key, kl.key);
            FooterPartitionPack footerPartitionPack = MxfValue.parseValue(in, kl, FooterPartitionPack.class);
            s.footerKLV = kl;
            s.setFooterPartitionPack(footerPartitionPack);
            long headerPPOffset = headerPartitionPack.ThisPartition.get();
            long previousPartitionOffset = footerPartitionPack.PreviousPartition.get();
            //            System.out.println("footer @" + kl.offset);
            //            System.out.println(footerPartitionPack.toDebugString());

            //read backwards partitions and indexes
            TreeMap<KLV, MxfValue> bodyKLVs = s.bodyKLVs;
            TreeMap<KLV, MxfValue> indexKLVs = s.indexKLVs;
            do {
                in.seek(previousPartitionOffset);
                kl = KLV.readKL(in);
                System.out.println("pos in file " + kl.offset + " 0x" + Long.toHexString(kl.offset));
                if (BodyPartitionPack.Key.equals(kl.key)) {
                    BodyPartitionPack bpp = MxfValue.parseValue(in, kl, BodyPartitionPack.class);
                    //System.out.println(bpp.toDebugString());
                    //sanity check: make sure partition offsets are actually going backwards
                    assertTrue(bpp.PreviousPartition.get() < previousPartitionOffset);
                    bodyKLVs.put(kl, bpp);
                    previousPartitionOffset = bpp.PreviousPartition.get();
                    if (bpp.IndexByteCount.get() > 0 && bpp.IndexSID.get() > 0) {
                        //index follows
                        kl = KLV.readKL(in);
                        assertEquals(IndexTable.Key, kl.key);
                        IndexTable indexTable = MxfValue.parseValue(in, kl, IndexTable.class);
                        indexKLVs.put(kl, indexTable);
                    }
                } else if (HeaderPartitionPack.Key.equals(kl.key)) {
                    break;
                } else {
                    throw new IllegalStateException();
                }

            } while (previousPartitionOffset != headerPPOffset);

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
        for (Entry<KLV, MxfValue> entry : indexKLVs.entrySet()) {
            IndexTable idx = (IndexTable) entry.getValue();
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
        if (HeaderPartitionPack.Key.equals(k0.key)) {
            headerPartitionPack = MxfValue.parseValue(in, k0, HeaderPartitionPack.class);
        }
        TreeMap<KLV, MxfValue> headerKLVs = new TreeMap<KLV, MxfValue>();

        LocalTagsIndex idx = null;
        if (headerPartitionPack != null && headerPartitionPack.HeaderByteCount.get() > 0) {
            headerKLVs.put(k0, headerPartitionPack);
            while (in.position() < headerPartitionPack.HeaderByteCount.get()) {
                KLV k = KLV.readKL(in);
                System.out.println("pos in file " + k.offset + " 0x" + Long.toHexString(k.offset));
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

}