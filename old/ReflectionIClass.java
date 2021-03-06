
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

import com.badlogic.gdx.utils.reflect.*;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.InternalCompilerException;
import org.codehaus.commons.compiler.Location;
import org.codehaus.commons.nullanalysis.Nullable;


/**
 * Wraps a {@link Class} in an {@link IClass}.
 */
class ReflectionIClass extends IClass {

    private final Class<?> clazz;
    private final IClassLoader iClassLoader;

    /**
     * @param iClassLoader Required to load other {@link IClass}es on {@code get...()}
     */
    ReflectionIClass(Class<?> clazz, IClassLoader iClassLoader) {
        this.clazz = clazz;
        this.iClassLoader = iClassLoader;
    }

    @Override
    protected IConstructor[]
    getDeclaredIConstructors2() {
        Constructor[] constructors = ClassReflection.getConstructors(clazz);
        IConstructor[] result = new IConstructor[constructors.length];
        for (int i = 0; i < constructors.length; ++i) {
            result[i] = new ReflectionIConstructor(constructors[i]);
        }
        return result;
    }

    @Override
    protected IMethod[]
    getDeclaredIMethods2() {
        Method[] methods = ClassReflection.getDeclaredMethods(clazz);

        if (methods.length == 0 && this.clazz.isArray()) {

            // Arrays have ONE single method: "Object clone()".
            return new IMethod[]{new IMethod() {
                @Override
                public IAnnotation[] getAnnotations() {
                    return new IAnnotation[0];
                }

                @Override
                public Access getAccess() {
                    return Access.PUBLIC;
                }

                @Override
                public boolean isStatic() {
                    return false;
                }

                @Override
                public boolean isAbstract() {
                    return false;
                }

                @Override
                public String getName() {
                    return "clone";
                }

                @Override
                public IClass[] getParameterTypes2() {
                    return new IClass[0];
                }

                @Override
                public boolean isVarargs() {
                    return false;
                }

                @Override
                public IClass[] getThrownExceptions2() {
                    return new IClass[0];
                }

                @Override
                public IClass
                getReturnType() {
                    return ReflectionIClass.this.iClassLoader.TYPE_java_lang_Object;
                }
            }};
        }

        return this.methodsToIMethods(methods);
    }

    @Override
    protected IField[]
    getDeclaredIFields2() {
        return this.fieldsToIFields(ClassReflection.getDeclaredFields(clazz));
    }

    @Override
    protected IClass[]
    getDeclaredIClasses2() {
        return this.classesToIClasses(this.clazz.getDeclaredClasses());
    }

    @Override
    @Nullable
    protected IClass
    getDeclaringIClass2() {

        Class<?> declaringClass = this.clazz.getDeclaringClass();
        return declaringClass == null ? null : this.classToIClass(declaringClass);
    }

    @Override
    @Nullable
    protected IClass
    getOuterIClass2() throws CompileException {

//        if (Modifier.isStatic(this.clazz.getModifiers())) return null;

        return this.getDeclaringIClass();
    }

    @Override
    @Nullable
    protected IClass
    getSuperclass2() {

        Class<?> superclass = this.clazz.getSuperclass();
        return superclass == null ? null : this.classToIClass(superclass);
    }

    @Override
    @Nullable
    protected IClass
    getComponentType2() {
        Class<?> componentType = this.clazz.getComponentType();
        return componentType == null ? null : this.classToIClass(componentType);
    }

    @Override
    protected IClass[] getInterfaces2() {
        return this.classesToIClasses(this.clazz.getInterfaces());
    }

    @Override
    protected String getDescriptor2() {
        return Descriptor.fromClassName(this.clazz.getName());
    }

    @Override
    public Access getAccess() {
        return ReflectionIClass.modifiers2Access(this.clazz);
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return this.clazz.isEnum();
    }

    @Override
    public boolean isInterface() {
        return this.clazz.isInterface();
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isArray() {
        return this.clazz.isArray();
    }

    @Override
    public boolean
    isPrimitive() {
        return this.clazz.isPrimitive();
    }

    @Override
    public boolean
    isPrimitiveNumeric() {
        return (
                this.clazz == byte.class
                        || this.clazz == short.class
                        || this.clazz == int.class
                        || this.clazz == long.class
                        || this.clazz == char.class
                        || this.clazz == float.class
                        || this.clazz == double.class
        );
    }

    @Override
    public IAnnotation[]
    getIAnnotations2() throws CompileException {

        Annotation[] as = ClassReflection.getAnnotations(clazz);
        if (as.length == 0) return IClass.NO_ANNOTATIONS;

        IAnnotation[] result = new IAnnotation[as.length];
        for (int i = 0; i < as.length; i++) {
            final Annotation a = as[i];

            // Get annotation type IClass.
            final Class<? extends java.lang.annotation.Annotation> annotationType = a.getAnnotationType();
            final IClass annotationTypeIClass;
            try {
                annotationTypeIClass = ReflectionIClass.this.iClassLoader.loadIClass(
                        Descriptor.fromClassName(annotationType.getName())
                );
            } catch (ClassNotFoundException cnfe) {
                throw new CompileException(
                        "Loading annotation type",
                        null, // location
                        cnfe
                );
            }
            if (annotationTypeIClass == null) {
                throw new CompileException("Could not load \"" + annotationType.getName() + "\"", null);
            }

            result[i] = new IAnnotation() {

                @Override
                public IClass
                getAnnotationType() {
                    return annotationTypeIClass;
                }

                @Override
                public Object
                getElementValue(String name) throws CompileException {
                    try {
                        Object v = a.getClass().getMethod(name).invoke(a);

                        if (!Enum.class.isAssignableFrom(v.getClass())) return v;

                        Class<?> enumClass = v.getClass();

                        String enumConstantName = (String) enumClass.getMethod("name").invoke(v);

                        IClass enumIClass = ReflectionIClass.this.classToIClass(enumClass);

                        IField enumConstField = enumIClass.getDeclaredIField(enumConstantName);
                        if (enumConstField == null) {
                            throw new CompileException((
                                    "Enum \""
                                            + enumIClass
                                            + "\" has no constant \""
                                            + enumConstantName
                                            + ""
                            ), null);
                        }
                        return enumConstField;
                    } catch (NoSuchMethodException e) {
                        throw new CompileException(
                                "Annotation \"" + annotationType.getName() + "\" has no element \"" + name + "\"",
                                null
                        );
                    } catch (Exception e) {
                        throw new AssertionError(e);
                    }
                }

                @Override
                public String
                toString() {
                    return "@" + annotationTypeIClass;
                }
            };
        }

        return result;
    }

    /**
     * @return The underlying {@link Class java.lang.Class}
     */
    public Class<?>
    getClazz() {
        return this.clazz;
    }

    /**
     * @return E.g. {@code "int"}, {@code "int[][]"}, {@code "pkg1.pkg2.Outer$Inner[]"}
     */
    @Override
    public String
    toString() {
        int brackets = 0;
        Class<?> c = this.clazz;
        while (c.isArray()) {
            ++brackets;
            c = c.getComponentType();
        }
        String s = c.getName();

        // Must not strip "java.lang.", because some pieces of code rely on "toString()" returning the COMPLETE
        // qualified name.
//        if (s.startsWith("java.lang.")) s = s.substring(10);

        while (brackets-- > 0) s += "[]";
        return s;
    }

    private
    class ReflectionIConstructor extends IConstructor {

        ReflectionIConstructor(Constructor constructor) {
            this.constructor = constructor;
        }

        // Implement IMember.
        @Override
        public Access
        getAccess() {
            return ReflectionIClass.modifiers2Access(this.constructor);
        }

        @Override
        public IAnnotation[]
        getAnnotations() {
            return new IAnnotation[0];
        }

        @Override
        public boolean
        isVarargs() {
            // TRANSIENT is identical with VARARGS.
            return true;
        }

        // Implement "IConstructor".
        @Override
        public IClass[]
        getParameterTypes2() throws CompileException {
            IClass[] parameterTypes = ReflectionIClass.this.classesToIClasses(this.constructor.getParameterTypes());

            // The JAVADOC of java.lang.reflect.Constructor does not document it, but "getParameterTypes()" includes
            // the synthetic "enclosing instance" parameter.
            IClass outerClass = ReflectionIClass.this.getOuterIClass();
            if (outerClass != null) {
                if (parameterTypes.length < 1) {
                    throw new CompileException(
                            "Constructor \"" + this.constructor + "\" lacks synthetic enclosing instance parameter",
                            null
                    );
                }
                if (parameterTypes[0] != outerClass) {
                    throw new CompileException((
                            "Enclosing instance parameter of constructor \""
                                    + this.constructor
                                    + "\" has wrong type -- \""
                                    + parameterTypes[0]
                                    + "\" vs. \""
                                    + outerClass
                                    + "\""
                    ), null);
                }
                IClass[] tmp = new IClass[parameterTypes.length - 1];
                System.arraycopy(parameterTypes, 1, tmp, 0, tmp.length);
                parameterTypes = tmp;
            }

            return parameterTypes;
        }

        @Override
        public MethodDescriptor
        getDescriptor2() {
            Class<?>[] parameterTypes = this.constructor.getParameterTypes();
            String[] parameterDescriptors = new String[parameterTypes.length];
            for (int i = 0; i < parameterDescriptors.length; ++i) {
                parameterDescriptors[i] = Descriptor.fromClassName(parameterTypes[i].getName());
            }
            return new MethodDescriptor(Descriptor.VOID, parameterDescriptors);
        }

        @Override
        public IClass[]
        getThrownExceptions2() {
            return null;
        }

        final Constructor constructor;
    }

    public
    class ReflectionIMethod extends IMethod {

        ReflectionIMethod(Method method) {
            this.method = method;
        }

        // Implement IMember.
        @Override
        public Access
        getAccess() {
            return ReflectionIClass.modifiers2Access(this.method);
        }

        @Override
        public IAnnotation[]
        getAnnotations() {
            return new IAnnotation[0];
        }

        // Implement "IMethod".
        @Override
        public String
        getName() {
            return this.method.getName();
        }

        @Override
        public boolean
        isVarargs() {

            // VARARGS is identical with TRANSIENT.
            return this.method.isVarArgs();
        }

        @Override
        public IClass[]
        getParameterTypes2() {
            return ReflectionIClass.this.classesToIClasses(this.method.getParameterTypes());
        }

        @Override
        public boolean
        isStatic() {
            return this.method.isStatic();
        }

        @Override
        public boolean
        isAbstract() {
            return this.method.isAbstract();
        }

        @Override
        public IClass
        getReturnType() {
            return ReflectionIClass.this.classToIClass(this.method.getReturnType());
        }

        @Override
        public IClass[]
        getThrownExceptions2() {
            return null;
        }

        private final Method method;
    }

    private
    class ReflectionIField extends IField {

        ReflectionIField(Field field) {
            this.field = field;
        }

        // Implement IMember.
        @Override
        public Access
        getAccess() {
            return ReflectionIClass.modifiers2Access(this.field);
        }

        @Override
        public IAnnotation[]
        getAnnotations() {
            return new IAnnotation[0];
        }

        // Implement "IField".

        @Override
        public String
        getName() {
            return this.field.getName();
        }

        @Override
        public boolean
        isStatic() {
            return this.field.isStatic();
        }

        @Override
        public IClass
        getType() {
            return ReflectionIClass.this.classToIClass(this.field.getType());
        }

        @Override
        public String
        toString() {
            return Descriptor.toString(this.getDeclaringIClass().getDescriptor()) + "." + this.getName();
        }

        /**
         * This implementation of {@link IField#getConstantValue()} is not completely correct:
         * <ul>
         *   <li>
         *     It treats non-static fields as non-constant
         *   </li>
         *   <li>
         *     Even fields with a <em>non-constant</em> initializer are identified as constant. (The value of that
         *     field may be different in a different JVM instance -- the classical example is {@link
         *     java.io.File#separator}.)
         *   </li>
         * </ul>
         * <p>
         *   Notice that enum constants are <em>not</em> constant expression (despite the similarity of names).
         * </p>
         */
        @Override
        public Object
        getConstantValue() throws CompileException {
            Class<?> clazz = this.field.getType();
            if (
                    this.field.isStatic()
                            && this.field.isFinal()
                            && (clazz.isPrimitive() || clazz == String.class)
            ) {
                try {
                    return this.field.get(null);
                } catch (ReflectionException ex) {
                    throw new CompileException( // SUPPRESS CHECKSTYLE AvoidHidingCause
                            "Field \"" + this.field.getName() + "\" is not accessible",
                            (Location) null
                    );
                }
            }
            return IClass.NOT_CONSTANT;
        }

        final Field field;
    }

    /**
     * Loads {@link Class} through {@link IClassLoader} to ensure unique {@link IClass}es.
     */
    private IClass
    classToIClass(Class<?> c) {
        IClass iClass;
        try {
            iClass = this.iClassLoader.loadIClass(Descriptor.fromClassName(c.getName()));
        } catch (ClassNotFoundException ex) {
            throw new InternalCompilerException("Loading IClass \"" + c.getName() + "\": " + ex);
        }
        if (iClass == null) {
            throw new InternalCompilerException((
                    "Cannot load class \"" + c.getName() + "\" through the given ClassLoader"
            ));
        }
        return iClass;
    }

    /**
     * @see #classToIClass(Class)
     */
    private IClass[]
    classesToIClasses(Class<?>[] cs) {

        IClass[] result = new IClass[cs.length];
        for (int i = 0; i < cs.length; ++i) result[i] = this.classToIClass(cs[i]);

        return result;
    }

    private IMethod[]
    methodsToIMethods(Method[] methods) {

        IMethod[] result = new IMethod[methods.length];
        for (int i = 0; i < result.length; i++) result[i] = new ReflectionIMethod(methods[i]);

        return result;
    }

    private IField[]
    fieldsToIFields(Field[] fields) {

        IField[] result = new IField[fields.length];
        for (int i = 0; i < fields.length; ++i) result[i] = new ReflectionIField(fields[i]);

        return result;
    }

    private static Access
    modifiers2Access(Field mod) {
        return (
                mod.isPrivate() ? Access.PRIVATE :
                        mod.isProtected() ? Access.PROTECTED :
                                mod.isPublic() ? Access.PUBLIC :
                                        Access.DEFAULT
        );
    }

    private static Access modifiers2Access(Method mod) {
        return (
                mod.isPrivate() ? Access.PRIVATE :
                        mod.isProtected() ? Access.PROTECTED :
                                mod.isPublic() ? Access.PUBLIC :
                                        Access.DEFAULT
        );
    }

    private static Access modifiers2Access(Constructor mod) {
        return (
                mod.isAccessible() ? Access.PUBLIC :Access.PRIVATE
        );
    }

    private static Access modifiers2Access(Class mod) {
        return (
                                        Access.PUBLIC

        );
    }

}
