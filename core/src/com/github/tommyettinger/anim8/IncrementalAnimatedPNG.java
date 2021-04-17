package com.github.tommyettinger.anim8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StreamUtils;
import com.klemstinegroup.sunshineblue.engine.util.MemoryFileHandle;
import net.sf.jazzlib.Deflater;
import net.sf.jazzlib.DeflaterOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Full-color animated PNG encoder with compression.
 * This type of animated PNG supports both full color and a full alpha channel; it
 * does not reduce the colors to match a palette. If your image does not have a full
 * alpha channel and has 256 or fewer colors, you can use {@link AnimatedGif} or the
 * animated mode of {@link PNG8}, which have comparable APIs. An instance can be
 * reused to encode multiple animated PNGs with minimal allocation.
 * <br>
 * <pre>
 * Copyright (c) 2007 Matthias Mann - www.matthiasmann.de
 * Copyright (c) 2014 Nathan Sweet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * </pre>
 *
 * @author Matthias Mann
 * @author Nathan Sweet
 * @author Tommy Ettinger
 */
public class IncrementalAnimatedPNG implements Disposable {
    static private final byte[] SIGNATURE = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
    static private final int IHDR = 0x49484452, acTL = 0x6163544C,
            fcTL = 0x6663544C, IDAT = 0x49444154,
            fdAT = 0x66644154, IEND = 0x49454E44;
    static private final byte COLOR_ARGB = 6;
    static private final byte COMPRESSION_DEFLATE = 0;
    static private final byte FILTER_NONE = 0;
    static private final byte INTERLACE_NONE = 0;
    static private final byte PAETH = 4;

    private final ChunkBuffer buffer;
    private final Deflater deflater;
    private ByteArray lineOutBytes, curLineBytes, prevLineBytes;
    private boolean flipY = true;
    private int lastLineLen;
    private int width;
    private int height;
    private int lineLen;
    private DeflaterOutputStream deflaterOutput;
    private DataOutputStream dataOutput;
    private short fps;
    //    private OutputStream output;
    private MemoryFileHandle mfh;
    private OutputStream output;

    /**
     * Creates an AnimatedPNG writer with an initial buffer size of 16384. The buffer can resize later if needed.
     */
    public IncrementalAnimatedPNG() {
        this(16384);
    }

    /**
     * Creates an AnimatedPNG writer with the given initial buffer size. The buffer can resize if needed, so using a
     * small size is only a problem if it slows down writing by forcing a resize for several parts of a PNG. A default
     * of 16384 is reasonable.
     *
     * @param initialBufferSize the initial size for the buffer that stores PNG chunks; 16384 is a reasonable default
     */
    public IncrementalAnimatedPNG(int initialBufferSize) {
        buffer = new ChunkBuffer(initialBufferSize);
        deflater = new Deflater();
    }

    /**
     * If true, the resulting AnimatedPNG is flipped vertically. Default is true.
     */
    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    /**
     * Sets the deflate compression level. Default is {@link Deflater#DEFAULT_COMPRESSION}, which is currently 6 on all
     * Java versions in the 8 to 14 range, but is permitted to change.
     */
    public void setCompression(int level) {
        deflater.setLevel(level);
    }

    byte[] lineOut, curLine, prevLine;
    int seq = 0;

    public void start(MemoryFileHandle mfh, short fps, int width, int height) {
        this.mfh = mfh;
        this.width = width;
        this.height = height;
        this.fps = fps;
        deflaterOutput = new DeflaterOutputStream(buffer, deflater);
        this.output = mfh.write(false);
        dataOutput = new DataOutputStream(this.output);
        try {
            dataOutput.write(SIGNATURE);

            buffer.writeInt(IHDR);
            buffer.writeInt(width);
            buffer.writeInt(height);
            buffer.writeByte(8); // 8 bits per component.
            buffer.writeByte(COLOR_ARGB);
            buffer.writeByte(COMPRESSION_DEFLATE);
            buffer.writeByte(FILTER_NONE);
            buffer.writeByte(INTERLACE_NONE);
            buffer.endChunk(dataOutput);

            buffer.writeInt(acTL);
            buffer.writeInt(0xff);
            buffer.writeInt(0);
            buffer.endChunk(dataOutput);

            lineLen = width * 4;

        } catch (IOException e) {
            Gdx.app.error("anim8", e.getMessage());
        }
    }

    int cntt = 0;

    public void write(Pixmap pixmap) {
        try {
            buffer.writeInt(fcTL);
            buffer.writeInt(seq++);
            buffer.writeInt(width);
            buffer.writeInt(height);
            buffer.writeInt(0);
            buffer.writeInt(0);
            buffer.writeShort(1);
            buffer.writeShort(fps);
            buffer.writeByte(1);
            buffer.writeByte(1);
            buffer.endChunk(dataOutput);

            if (cntt++ == 0) {
                buffer.writeInt(IDAT);
            } else {
//                pixmap = frames.get(i);
                buffer.writeInt(fdAT);
                buffer.writeInt(seq++);
            }
            deflater.reset();

            if (lineOutBytes == null) {
                lineOut = (lineOutBytes = new ByteArray(lineLen)).items;
                curLine = (curLineBytes = new ByteArray(lineLen)).items;
                prevLine = (prevLineBytes = new ByteArray(lineLen)).items;
            } else {
                lineOut = lineOutBytes.ensureCapacity(lineLen);
                curLine = curLineBytes.ensureCapacity(lineLen);
                prevLine = prevLineBytes.ensureCapacity(lineLen);
                for (int ln = 0, n = lastLineLen; ln < n; ln++)
                    prevLine[ln] = 0;
            }
            lastLineLen = lineLen;

//                pixels = pixmap.getPixels();
//                oldPosition = ((ByteBuffer)pixels).position();
            for (int y = 0; y < height; y++) {
                int py = flipY ? (height - y - 1) : y;
//                    if (rgba8888) {
//                        ((ByteBuffer)pixels).position(py * lineLen);
//                        pixels.get(curLine, 0, lineLen);
//                    } else {
                for (int px = 0, x = 0; px < width; px++) {
                    int pixel = pixmap.getPixel(px, py);
                    curLine[x++] = (byte) ((pixel >>> 24) & 0xff);
                    curLine[x++] = (byte) ((pixel >>> 16) & 0xff);
                    curLine[x++] = (byte) ((pixel >>> 8) & 0xff);
                    curLine[x++] = (byte) (pixel & 0xff);
                }
//                    }

                lineOut[0] = (byte) (curLine[0] - prevLine[0]);
                lineOut[1] = (byte) (curLine[1] - prevLine[1]);
                lineOut[2] = (byte) (curLine[2] - prevLine[2]);
                lineOut[3] = (byte) (curLine[3] - prevLine[3]);

                for (int x = 4; x < lineLen; x++) {
                    int a = curLine[x - 4] & 0xff;
                    int b = prevLine[x] & 0xff;
                    int c = prevLine[x - 4] & 0xff;
                    int p = a + b - c;
                    int pa = p - a;
                    if (pa < 0) pa = -pa;
                    int pb = p - b;
                    if (pb < 0) pb = -pb;
                    int pc = p - c;
                    if (pc < 0) pc = -pc;
                    if (pa <= pb && pa <= pc)
                        c = a;
                    else if (pb <= pc) //
                        c = b;
                    lineOut[x] = (byte) (curLine[x] - c);
                }

                deflaterOutput.write(PAETH);
                deflaterOutput.write(lineOut, 0, lineLen);

                byte[] temp = curLine;
                curLine = prevLine;
                prevLine = temp;
            }
//                ((ByteBuffer) pixels).position(oldPosition);
            deflaterOutput.finish();
            buffer.endChunk(dataOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pixmap.dispose();
    }

    public void end() {
        try {
            buffer.writeInt(IEND);
            buffer.endChunk(dataOutput);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for (int i=40;i<50;i++){
//            System.out.println(i+"\t"+(mfh.ba.get(i)&0xff));
//        }
        StreamUtils.closeQuietly(output);
//        mfh.ba.set(44,(byte)((cntt>>>0)&0xff));
//        mfh.ba.set(43,(byte)((cntt>>>8)&0xff));
//        mfh.ba.set(42,(byte)((cntt>>>16)&0xff));
//        mfh.ba.set(41,(byte)((cntt>>>24)&0xff));
//        for (int i=40;i<50;i++){
//            System.out.println(i+"\t"+(mfh.ba.get(i)&0xff));
//        }
        MemoryFileHandle mgg = new MemoryFileHandle();
        buffer.buffer.reset();
        dataOutput = new DataOutputStream(mgg.write(false));
        try {
            dataOutput.write(SIGNATURE);

            buffer.writeInt(IHDR);
            buffer.writeInt(width);
            buffer.writeInt(height);
            buffer.writeByte(8); // 8 bits per component.
            buffer.writeByte(COLOR_ARGB);
            buffer.writeByte(COMPRESSION_DEFLATE);
            buffer.writeByte(FILTER_NONE);
            buffer.writeByte(INTERLACE_NONE);
            buffer.endChunk(dataOutput);

            buffer.writeInt(acTL);
            buffer.writeInt(cntt);
            buffer.writeInt(0);
            buffer.endChunk(dataOutput);

            dataOutput.close();
            for (int i = 0; i < mgg.ba.size; i++) {
//                System.out.println(i + ":\t" + mfh.ba.get(i) + "\t" + mgg.ba.get(i));
                mfh.ba.set(i, mgg.ba.get(i));
            }
        } catch (IOException e) {
            Gdx.app.error("anim8", e.getMessage());
        }


        dispose();
    }

    /**
     * Disposal should probably be done explicitly, especially if using JRE versions after 8.
     * In Java 8 and earlier, you could rely on finalize() doing what this does, but that isn't
     * a safe assumption in Java 9 and later. Note, don't use the same AnimatedPNG object after you call
     * this method; you'll need to make a new one if you need to write again after disposing.
     */
    @Override
    public void dispose() {
        deflater.end();
    }
}
