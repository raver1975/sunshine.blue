
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

package org.codehaus.commons.compiler.util.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * A sequence of bytes that is identified by a "name", is typically found by a {@link
 * ResourceFinder}, can be {@link #open()}ed for reading, and optionally
 * has a {@link #lastModified()} property.
 * <p>
 *   There also exists a {@link ResourceCreator} concept which opens a
 *   resource for writing, but that happens directly and not through an intermediate {@link Resource} object.
 * </p>
 *
 * @see ResourceFinder
 * @see ResourceCreator
 */
public
interface Resource {

    /**
     * Opens the resource. The caller is responsible for closing the {@link InputStream}.
     */
    InputStream open() throws IOException;

    /**
     * Returns a decorative "file name" that can be used for reporting errors and the like. It does not necessarily map
     * to a file in the local file system!
     */
    String getFileName();

    /**
     * Returns the time of the last modification, in milliseconds since 1970, or {@code 0L} if the time of the last
     * modification cannot be determined.
     */
    long lastModified();
}
