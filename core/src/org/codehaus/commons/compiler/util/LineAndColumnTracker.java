
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2018 Arno Unkrig. All rights reserved.
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

package org.codehaus.commons.compiler.util;

import java.util.regex.Pattern;

/**
 * Keeps track of "line numbers" and "column numbers" while a char stream is being processed. Line breaks are
 * identified as defined by the {@code \R} pattern of {@link Pattern}. Initially, line number and column number are 1.
 * <p>
 *   This class has no public constructors, instead, use one of the static "factory methods".
 * </p>
 *
 * @see #create()
 */
public abstract
class LineAndColumnTracker {

    /**
     * The default "tab width".
     *
     * @see #setTabWidth(int)
     */
    public static final int DEFAULT_TAB_WIDTH = 8;

    /**
     * Reconfigures the TAB width. Value {@code 1} will treat TAB characters just like any other (non-line-break)
     * character. The default tab with is {@link #DEFAULT_TAB_WIDTH} ({@value #DEFAULT_TAB_WIDTH}).
     */
    public abstract void setTabWidth(int tabWidth);

    /**
     * Consumes the next character of the stream.
     */
    public abstract void consume(char c);

    /**
     * Returns the line number of the previously consumed character (starting at 1). Line separator characters belong
     * to the "next" line.
     */
    public abstract int getLineNumber();

    /**
     * Sets the "current line number" to the given value.
     */
    public abstract void setLineNumber(int lineNumber);

    /**
     * Returns the column number of the previously consumed character; 1 after a line separator character has been
     * consumed, 2 after the first non-line-separator character, and so forth.
     */
    public abstract int getColumnNumber();

    /**
     * Sets the "current column number" to the given value.
     */
    public abstract void setColumnNumber(int columnNumber);

    /**
     * Resets the current line number to 1 and the current column number to one. (The configured {@link
     * #setTabWidth(int) tab width} remains.)
     */
    public abstract void reset();

    /**
     * @return A {@link LineAndColumnTracker} instance
     */
    public static LineAndColumnTracker
    create() {

        return new LineAndColumnTracker() {

            // Configuration.
            private int tabWidth = LineAndColumnTracker.DEFAULT_TAB_WIDTH;

            // State.
            private int     line = 1, column = 1;
            private boolean crPending;

            @Override public void
            consume(char c) {

                if (this.crPending) {
                    this.crPending = false;
                    if (c == '\n') return;
                }

                switch (c) {
                case '\r':
                    this.crPending = true;
                    this.line++;
                    this.column = 1;
                    break;
                case '\n':
                case '\u0085': // "next-line character"
                case '\u2028': // "line-separator character"
                case '\u2029': // "paragraph-separator character"
                    this.line++;
                    this.column = 1;
                    break;
                case '\t':
                    this.column = this.column - ((this.column - 1) % this.tabWidth) + this.tabWidth;
                    break;
                default:
                    this.column++;
                    break;
                }
            }

            @Override public int  getLineNumber()                   { return this.line;           }
            @Override public void setLineNumber(int lineNumber)     { this.line = lineNumber;     }
            @Override public int  getColumnNumber()                 { return this.column;         }
            @Override public void setColumnNumber(int columnNumber) { this.column = columnNumber; }

            @Override public void setTabWidth(int tabWidth) { this.tabWidth = tabWidth; }

            @Override public void
            reset() {
                this.line      = (this.column = 1);
                this.crPending = false;
            }
        };
    }
}
