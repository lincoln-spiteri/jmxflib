package com.vg.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.commons.lang3.SystemUtils;


public class FileUtil {
    public static File tildeExpand(String path) {
        if (path.startsWith("~")) {
            path = path.replaceFirst("~", SystemUtils.getUserHome().getAbsolutePath());
        }
        return new File(path);
    }

    public static void writeFully(ByteBuffer data, String filePath) throws IOException {
        FileChannel channel = new FileOutputStream(filePath).getChannel();
        writeFully(data, channel);
        channel.close();
    }

    public static void writeFully(ByteBuffer data, WritableByteChannel channel) throws IOException {
        while (data.hasRemaining()) {
            channel.write(data);
        }
    }

    public static long forceSeek(SeekableInputStream raf, long offset) throws IOException {
        long length = raf.length();
        assertTrue("trying to seek to (" + (offset) + ") outside of len (" + length + ")", offset <= length);
        raf.seek(offset);
        assertEquals(offset, raf.position());
        return offset;
    }

    public static long forceSeek(RandomAccessFile raf, long offset) throws IOException {
        assertTrue(offset >= 0);
        long length = raf.length();
        assertTrue("trying to seek to (" + (offset) + ") outside of file len (" + length + ")", offset <= length);
        raf.seek(offset);
        assertEquals(offset, raf.getFilePointer());
        return offset;
    }

    public static void readFully(InputStream in, byte[] type) throws IOException {
        if (!FileUtil.tryReadFully(in, type)) {
            throw new EOFException();
        }
    }

    public static boolean tryReadFully(InputStream in, byte[] type) throws IOException {
        int n = 0;
        int len = type.length;
        while (n < len) {
            int count = in.read(type, 0 + n, len - n);
            if (count < 0) {
                return false;
            }
            n += count;
        }
        return true;
    }

    public static ByteBuffer readFullyOrDie(InputStream is, ByteBuffer frameBuf) throws IOException {
        ReadableByteChannel in = Channels.newChannel(is);
        while (frameBuf.hasRemaining()) {
            if (-1 == in.read(frameBuf)) {
                throw new IOException("buffer underflow");
            }
        }
        return frameBuf;
    }
}
