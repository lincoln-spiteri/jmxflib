package com.vg.mxf;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.vg.util.FileUtil;
import com.vg.util.SeekableInputStream;

public class MxfValue extends PackedStruct {
    public void parse() {
    }

    public UUID getInstanceUID() {
        return UUID.ZERO;
    }

    public List<UUID> getReferencedUIDs() {
        return Collections.emptyList();
    }

    public static <T extends MxfValue> T parseValue(SeekableInputStream in, KLV kl, Class<T> class1) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate((int) kl.len);
        FileUtil.readFullyOrDie(in, buf).flip();
        try {
            T value;
            value = class1.newInstance();
            value.setByteBuffer(buf, 0);
            value.parse();
            return value;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static int[] getLocalTags(Class<? extends MxfValue> c) {

        TreeSet<Integer> tags = new TreeSet<Integer>();
        for (Field field : c.getDeclaredFields()) {
            Tag annotation = field.getAnnotation(Tag.class);
            if (annotation != null) {
                int tag = annotation.tag();
                tags.add(tag);
            }
        }
        int[] array = new int[tags.size()];
        Iterator<Integer> iterator = tags.iterator();
        for (int i = 0; i < array.length; i++) {
            array[i] = iterator.next();
        }
        return array;
    }

}
