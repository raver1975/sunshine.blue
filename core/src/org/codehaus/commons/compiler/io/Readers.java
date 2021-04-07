
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2018, 2019 Arno Unkrig. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.commons.compiler.io;

import org.codehaus.commons.compiler.util.LineAndColumnTracker;
import org.codehaus.commons.nullanalysis.NotNullByDefault;
import org.codehaus.commons.nullanalysis.Nullable;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Utiltity methods related to {@link Reader}.
 */
public final
class Readers {

    private Readers() {}

    /**
     * Any attempts to read return an "end-of-input" condition.
     */
    public static final Reader EMPTY_READER = new StringReader("");

    /**
     * @return {@link FilterReader} that runs the <var>runnable</var> right before the first character is read
     */
    public static Reader
    onFirstChar(Reader in, final Runnable runnable) {

        return new FilterReader(in) {

            private boolean hadChars;

            @Override public int
            read() throws IOException {
                this.aboutToRead();
                return super.read();
            }

            @Override @NotNullByDefault(false) public int
            read(char[] cbuf, int off, int len) throws IOException {
                this.aboutToRead();
                return super.read(cbuf, off, len);
            }

            @Override public long
            skip(long n) throws IOException {
                this.aboutToRead();
                return super.skip(n);
            }

            private void
            aboutToRead() {
                if (!this.hadChars) {
                    runnable.run();
                    this.hadChars = true;
                }
            }
        };
    }

    /**
     * @return A {@link FilterReader} that tracks line and column numbers while characters are being read
     */
    public static Reader
    trackLineAndColumn(Reader in, final LineAndColumnTracker tracker) {

        return new FilterReader(in) {

            @Override public int
            read() throws IOException {
                int c = super.read();
                if (c >= 0) tracker.consume((char) c);
                return c;
            }

            @Override @NotNullByDefault(false) public int
            read(char[] cbuf, final int off, int len) throws IOException {
                if (len <= 0) return 0;
                int c = this.read();
                if (c < 0) return -1;
                cbuf[off] = (char) c;
                return 1;
            }

            @Override public long
            skip(final long n) throws IOException {
                if (n <= 0) return 0;
                int c = this.read();
                if (c < 0) return 0;
                return 1;
            }

            @Override public boolean markSupported()                             { return false;            }
            @Override public void    mark(int readAheadLimit) throws IOException { throw new IOException(); }
            @Override public void    reset() throws IOException                  { throw new IOException(); }
        };
    }

    public static Reader
    concat(Reader... delegates) { return Readers.concat(Arrays.asList(delegates)); }

    /**
     * @return Reads from the first element of the <var>delegates</var>, then, after EOI, from the second until EOI,
     *         and so forth
     */
    public static Reader
    concat(final Iterable<Reader> delegates) {

        return new Reader() {

            private final Iterator<Reader> delegateIterator = delegates.iterator();
            private Reader                 currentDelegate = Readers.EMPTY_READER;

            /**
             * Closes all delegates.
             */
            @Override public void
            close() throws IOException { for (Reader delegate : delegates) delegate.close(); }

            @Override public int
            read() throws IOException {
                for (;;) {
                    int result = this.currentDelegate.read();
                    if (result != -1) return result;
                    if (!this.delegateIterator.hasNext()) return -1;
                    this.currentDelegate = (Reader) this.delegateIterator.next();
                }
            }

            @Override public long
            skip(long n) throws IOException {
                for (;;) {
                    long result = this.currentDelegate.skip(n);
                    if (result != -1L) return result;
                    if (!this.delegateIterator.hasNext()) return 0;
                    this.currentDelegate = (Reader) this.delegateIterator.next();
                }
            }

            @Override public int
            read(@Nullable final char[] cbuf, final int off, final int len) throws IOException {
                for (;;) {
                    int result = this.currentDelegate.read(cbuf, off, len);
                    if (result != -1L) return result;
                    if (!this.delegateIterator.hasNext()) return -1;
                    this.currentDelegate = (Reader) this.delegateIterator.next();
                }
            }
        };
    }

    /**
     * @return A {@link Reader} that copies the bytes being passed through to a given {@link Writer}; this is in
     *         analogy with the UNIX "tee" command
     */
    public static Reader
    teeReader(Reader in, final Writer out, final boolean closeWriterOnEoi) {

        return new FilterReader(in) {

            @Override public void
            close() throws IOException {
                this.in.close();
                out.close();
            }

            @Override public int
            read() throws IOException {
                int c = this.in.read();
                if (c == -1) {
                    if (closeWriterOnEoi) {
                        out.close();
                    } else {
                        out.flush();
                    }
                } else {
                    out.write(c);
                }
                return c;
            }

            @Override public int
            read(@Nullable char[] cbuf, int off, int len) throws IOException {
                int bytesRead = this.in.read(cbuf, off, len);
                if (bytesRead == -1) {
                    if (closeWriterOnEoi) {
                        out.close();
                    } else {
                        out.flush();
                    }
                } else {
                    out.write(cbuf, off, bytesRead);
                }
                return bytesRead;
            }
        };
    }

    public static String
    readAll(Reader in) throws IOException {
        StringWriter sw = new StringWriter();
        Readers.copy(in, sw);
        return sw.toString();
    }

    public static void
    copy(Reader in, Writer out) throws IOException {
        char[] buffer = new char[8192];
        for (;;) {
            int n = in.read(buffer);
            if (n == -1) break;
            out.write(buffer, 0, n);
        }
    }
}
