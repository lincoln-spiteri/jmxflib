package com.vg.util;

import java.io.IOException;
import java.io.InputStream;

public class BER {

    public static final byte ASN_LONG_LEN = (byte) 0x80;

    public static final long decodeLength(InputStream is) throws IOException {
        long length = 0;
        int lengthbyte = is.read();
    
        if ((lengthbyte & ASN_LONG_LEN) > 0) {
            lengthbyte &= ~ASN_LONG_LEN; /* turn MSb off */
            if (lengthbyte == 0) {
                throw new IOException("Indefinite lengths are not supported");
            }
            if (lengthbyte > 8) {
                throw new IOException("Data length > 4 bytes are not supported!");
            }
            for (int i = 0; i < lengthbyte; i++) {
                long l = is.read() & 0xFF;
                length |= (l << (8 * ((lengthbyte - 1) - i)));
            }
            if (length < 0) {
                throw new IOException("mxflib does not support data lengths > 2^63");
            }
        } else { /* short asnlength */
            length = lengthbyte & 0xFF;
        }
        /**
         * If activated we do a length check here: length > is.available() ->
         * throw exception
         */
        //        if (checkLength) {
        //            checkLength(is, length);
        //        }
        return length;
    }

}
