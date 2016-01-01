# Introduction #

Jmxflib works for a limited set of mxf files right now since mxf spec is gigantic and ambiguous

# Example #

start with the following code:
```
SeekableInputStream in = new SeekableFileInputStream(new File("hello_world.mxf"));
MxfStructure structure = MxfStructure.readStructure(in);
```

MxfStructure is pretty much mxf headers converted to java objects, to get meaningful high level data you should query the appropriate headers e.g. to get stored dimension of video file

```
GenericPictureEssenceDescriptor pic = structure.getPictureEssenceDescriptor();
stored = pic.getStoredDimension();
display = pic.getDisplayRectangle();
```


# Simple MXF J2k demuxer #
encoded ByteBuffer will contain encoded j2k picture
```
SeekableInputStream in = new SeekableFileInputStream(new File("hello_world.mxf"));
long length = in.length();
do {
    KLV k = KLV.readKL(in);
    if (Registry.JPEG2000FrameWrappedPictureElement.matches(k.key)) {
        ByteBuffer encoded = ByteBuffer.allocate((int) k.len);
        encoded.limit((int) k.len);
        com.vg.util.FileUtil.readFullyOrDie(in, encoded);
        encoded.flip();
    } else {
        if (in.position() + k.len < length) {
            in.skip(k.len);
        } else {
            break;
        }
    }
} while (in.position() < length);
```