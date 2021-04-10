package com.github.tommyettinger.anim8;

import net.sf.jazzlib.CRC32;
import net.sf.jazzlib.CheckedOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Copied straight out of libGDX, in the PixmapIO class.
 */
class ChunkBuffer extends DataOutputStream {
    final ByteArrayOutputStream buffer;
    final CRC32 crc;

    ChunkBuffer(int initialSize) {
        this(new ByteArrayOutputStream(initialSize), new CRC32());
    }

    private ChunkBuffer(ByteArrayOutputStream buffer, CRC32 crc) {
        super(new CheckedOutputStream(buffer, crc));
        this.buffer = buffer;
        this.crc = crc;
    }

    public void endChunk(DataOutputStream target) throws IOException {
        flush();
        target.writeInt(buffer.size() - 4);
        buffer.writeTo(target);
        target.writeInt((int) crc.getValue());
        buffer.reset();
        crc.reset();
    }
}
