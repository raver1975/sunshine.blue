
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010 Arno Unkrig. All rights reserved.
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

package org.codehaus.janino;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.Location;
import org.codehaus.commons.compiler.WarningHandler;
import org.codehaus.commons.nullanalysis.Nullable;

import java.io.IOException;

/**
 * Standard implementation for the {@link TokenStream}.
 */
public
class TokenStreamImpl implements TokenStream {

    private final Scanner scanner;

    public
    TokenStreamImpl(Scanner scanner) { (this.scanner = scanner).setIgnoreWhiteSpace(true); }

    /**
     * The optional JAVADOC comment preceding the {@link #nextToken}.
     */
    @Nullable private String docComment;

    private Token
    produceToken() throws CompileException, IOException {

        for (;;) {
            Token token = this.scanner.produce();

            switch (token.type) {

            case WHITE_SPACE:
            case C_PLUS_PLUS_STYLE_COMMENT:
                break;

            case C_STYLE_COMMENT:
                if (token.value.startsWith("/**")) {
                    if (TokenStreamImpl.this.docComment != null) {
                        TokenStreamImpl.this.warning("MDC", "Misplaced doc comment", this.scanner.location());
                        TokenStreamImpl.this.docComment = null;
                    }
                }
                break;

            default:
                return token;
            }
        }
    }

    /**
     * Gets the text of the doc comment (a.k.a. "JAVADOC comment") preceding the next token.
     *
     * @return {@code null} if the next token is not preceded by a doc comment
     */
    @Nullable public String
    doc() {
        String s = this.docComment;
        this.docComment = null;
        return s;
    }

    @Nullable private Token previousToken, nextToken, nextButOneToken;

    @Override public Token
    peek() throws CompileException, IOException {
        if (this.nextToken == null) this.nextToken = this.produceToken();
        assert this.nextToken != null;
        return this.nextToken;
    }

    @Override public Token
    peekNextButOne() throws CompileException, IOException {
        if (this.nextToken == null) this.nextToken = this.produceToken();
        if (this.nextButOneToken == null) this.nextButOneToken = this.produceToken();
        assert this.nextButOneToken != null;
        return this.nextButOneToken;
    }

    @Override public boolean
    peek(String suspected) throws CompileException, IOException {
        return this.peek().value.equals(suspected);
    }

    @Override public int
    peek(String... suspected) throws CompileException, IOException {
        return TokenStreamImpl.indexOf(suspected, this.peek().value);
    }

    @Override public boolean
    peek(TokenType suspected) throws CompileException, IOException {
        return this.peek().type == suspected;
    }

    @Override public int
    peek(TokenType... suspected) throws CompileException, IOException {
        return TokenStreamImpl.indexOf(suspected, this.peek().type);
    }

    @Override public boolean
    peekNextButOne(String suspected) throws CompileException, IOException {
        return this.peekNextButOne().value.equals(suspected);
    }

    @Override public Token
    read() throws CompileException, IOException {

        if (this.nextToken == null) return (this.previousToken = this.produceToken());

        final Token result = this.nextToken;
        assert result != null;

        this.previousToken   = result;
        this.nextToken       = this.nextButOneToken;
        this.nextButOneToken = null;
        return result;
    }

    @Override public void
    read(String expected) throws CompileException, IOException {
        String s = this.read().value;
        if (!s.equals(expected)) throw this.compileException("'" + expected + "' expected instead of '" + s + "'");
    }

    @Override public int
    read(String... expected) throws CompileException, IOException {

        Token t = this.read();

        String value = t.value;

        int idx = TokenStreamImpl.indexOf(expected, value);
        if (idx != -1) return idx;

        if (value.startsWith(">")) {
            int result = TokenStreamImpl.indexOf(expected, ">");
            if (result != -1) {

                // The parser is "looking for" token ">", but the next token only STARTS with ">" (e.g. ">>="); split
                // that token into TWO tokens (">" and ">=", in the example).
                // See JLS8 3.2, "Lexical Transformation", last paragraph.
                Location loc = t.getLocation();
                this.nextToken = new Token(
                    loc.getFileName(),
                    loc.getLineNumber(),
                    loc.getColumnNumber() + 1,
                    TokenType.OPERATOR,
                    value.substring(1)
                );
                return result;
            }
        }

        throw this.compileException(
            "One of '"
            + TokenStreamImpl.join(expected, " ")
            + "' expected instead of '"
            + value
            + "'"
        );
    }

    @Override public String
    read(TokenType expected) throws CompileException, IOException {

        Token t = this.read();
        if (t.type != expected) {
            throw new CompileException(expected + " expected instead of '" + t.value + "'", t.getLocation());
        }

        return t.value;
    }

    @Override public int
    read(TokenType... expected) throws CompileException, IOException {

        Token t = this.read();

        TokenType type = t.type;

        int idx = TokenStreamImpl.indexOf(expected, type);
        if (idx != -1) return idx;

        throw this.compileException(
            "One of '"
            + TokenStreamImpl.join(expected, " ")
            + "' expected instead of '"
            + type
            + "'"
        );
    }

    @Override public boolean
    peekRead(String suspected) throws CompileException, IOException {

        if (this.nextToken != null) {
            if (!this.nextToken.value.equals(suspected)) return false;
            this.previousToken   = this.nextToken;
            this.nextToken       = this.nextButOneToken;
            this.nextButOneToken = null;
            return true;
        }

        Token result = this.produceToken();
        if (result.value.equals(suspected)) {
            this.previousToken = result;
            return true;
        }
        this.nextToken = result;
        return false;
    }

    @Override public int
    peekRead(String... suspected) throws CompileException, IOException {

        if (this.nextToken != null) {
            int idx = TokenStreamImpl.indexOf(suspected, this.nextToken.value);
            if (idx == -1) return -1;
            this.previousToken   = this.nextToken;
            this.nextToken       = this.nextButOneToken;
            this.nextButOneToken = null;
            return idx;
        }

        Token t   = this.produceToken();
        int   idx = TokenStreamImpl.indexOf(suspected, t.value);
        if (idx != -1) {
            this.previousToken = t;
            return idx;
        }
        this.nextToken = t;
        return -1;
    }

    @Override @Nullable public String
    peekRead(TokenType suspected) throws CompileException, IOException {

        Token nt = this.nextToken;
        if (nt != null) {
            if (nt.type != suspected) return null;
            this.previousToken   = this.nextToken;
            this.nextToken       = this.nextButOneToken;
            this.nextButOneToken = null;
            return nt.value;
        }

        nt = this.produceToken();
        if (nt.type == suspected) {
            this.previousToken = nt;
            return nt.value;
        }

        return (this.nextToken = nt).value;
    }

    @Override public int
    peekRead(TokenType... suspected) throws CompileException, IOException {

        if (this.nextToken != null) {
            int idx = TokenStreamImpl.indexOf(suspected, this.nextToken.type);
            if (idx != -1) {
                this.previousToken   = this.nextToken;
                this.nextToken       = this.nextButOneToken;
                this.nextButOneToken = null;
            }
            return idx;
        }

        Token t   = this.produceToken();
        int   idx = TokenStreamImpl.indexOf(suspected, t.type);
        if (idx != -1) {
            this.previousToken = t;
            return idx;
        }

        this.nextToken = t;
        return -1;
    }

    @Override public Location
    location() {
        return this.previousToken != null ? this.previousToken.getLocation() : this.scanner.location();
    }

    private static int
    indexOf(String[] strings, String subject) {
        for (int i = 0; i < strings.length; ++i) {
            if (strings[i].equals(subject)) return i;
        }
        return -1;
    }

    private static int
    indexOf(TokenType[] tta, TokenType subject) {
        for (int i = 0; i < tta.length; ++i) {
            if (tta[i].equals(subject)) return i;
        }
        return -1;
    }

    @Override public void
    setWarningHandler(@Nullable WarningHandler warningHandler) {
        this.warningHandler = warningHandler;
    }

    // Used for elaborate warning handling.
    @Nullable private WarningHandler warningHandler;

    @Override public String
    toString() { return this.nextToken + "/" + this.nextButOneToken + "/" + this.scanner.location(); }

    /**
     * Issues a warning with the given message and location and returns. This is done through
     * a {@link WarningHandler} that was installed through
     * {@link #setWarningHandler(WarningHandler)}.
     * <p>
     * The {@code handle} argument qualifies the warning and is typically used by
     * the {@link WarningHandler} to suppress individual warnings.
     *
     * @throws CompileException The optionally installed {@link WarningHandler} decided to throw a {@link
     *                          CompileException}
     */
    private void
    warning(String handle, String message, @Nullable Location location) throws CompileException {
        if (this.warningHandler != null) {
            this.warningHandler.handleWarning(handle, message, location);
        }
    }

    /**
     * Convenience method for throwing a {@link CompileException}.
     */
    protected final CompileException
    compileException(String message) { return new CompileException(message, this.scanner.location()); }

    private static String
    join(@Nullable Object[] oa, String glue) {

        if (oa == null) return ("(null)");

        if (oa.length == 0) return ("(zero length array)");

        StringBuilder sb = new StringBuilder().append(oa[0]);
        for (int i = 1; i < oa.length; ++i) sb.append(glue).append(oa[i]);

        return sb.toString();
    }
}
