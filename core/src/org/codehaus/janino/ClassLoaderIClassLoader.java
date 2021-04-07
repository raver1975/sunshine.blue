
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

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import org.codehaus.commons.nullanalysis.Nullable;


/**
 * An {@link IClassLoader} that loads {@link IClass}es through a reflection {@link ClassLoader}.
 */
public
class ClassLoaderIClassLoader extends IClassLoader {


    /**
     * @param classLoader The delegate that loads the classes
     */
    public
    ClassLoaderIClassLoader() {
        super(
            null   // parentIClassLoader
        );

        super.postConstruct();
    }

//    /**
//     * Equivalent to
//     * <pre>
//     *   ClassLoaderIClassLoader(Thread.currentThread().getContextClassLoader())
//     * </pre>
//     */
//    public
//    ClassLoaderIClassLoader() { this(Thread.currentThread().getContextClassLoader()); }
//
//    /**
//     * @return The delegate {@link ClassLoader}
//     */
//    public ClassLoader
//    getClassLoader() { return this.classLoader; }

    @Override @Nullable protected IClass
    findIClass(String descriptor) throws ClassNotFoundException {

        Class<?> clazz = null;
        try {
            clazz = ClassReflection.forName(Descriptor.toClassName(descriptor));
        } catch (ReflectionException e) {

            // Determine whether the class DOES NOT EXIST, or whether there were problems loading it. That's easier
            // said than done... the following seems to work:
            // (See also https://github.com/janino-compiler/janino/issues/104).
            {
                Throwable t = e.getCause();
                while (t instanceof ClassNotFoundException) t = t.getCause();
                if (t == null) return null;
            }
            try {
                throw e;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }


        IClass result = new ReflectionIClass(clazz, this);
        this.defineIClass(result);
        return result;
    }

}
