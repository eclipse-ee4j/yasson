/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import org.junit.jupiter.api.*;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

@Disabled
class AnnotationIntrospectorTestFixtures {

    public static interface ProvidesParameterRepresentation {
        Object[] asParameters();
    }

    private static final Map<String, Type> twoParameters(String name1, Type type1, String name2, Type type2) {
        Map<String, Type> parameters = new HashMap<>();
        parameters.put(name1, type1);
        parameters.put(name2, type2);
        return parameters;
    }

    public static Constructor<?>[] constructorsOf(Class<?> clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Constructor<?>[]>) clazz::getDeclaredConstructors);
    }

    public static class ObjectWithoutAnnotatedConstructor implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithoutAnnotatedConstructor("a string", Long.MAX_VALUE);
        }

        public ObjectWithoutAnnotatedConstructor(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithNotAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithJsonbCreatorAnnotatedConstructor implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithJsonbCreatorAnnotatedConstructor("a string", Long.MAX_VALUE);
        }

        @JsonbCreator
        public ObjectWithJsonbCreatorAnnotatedConstructor( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithJsonbCreatorAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithJsonbCreatorAnnotatedProtectedConstructor implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithJsonbCreatorAnnotatedProtectedConstructor("a string", Long.MAX_VALUE);
        }

        @JsonbCreator
        protected ObjectWithJsonbCreatorAnnotatedProtectedConstructor( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithJsonbCreatorAnnotatedProtectedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor implements ProvidesParameterRepresentation {
        private String string;
        private long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor("a string", Long.MAX_VALUE);
        }

        public ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor() {
            super();
        }

        @JsonbCreator
        protected ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(long primitive) {
            this.primitive = primitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithJsonbCreatorAnnotatedFactoryMethod implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithJsonbCreatorAnnotatedFactoryMethod("text", Long.MIN_VALUE);
        }

        @JsonbCreator
        public static final ObjectWithJsonbCreatorAnnotatedFactoryMethod create( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            return new ObjectWithJsonbCreatorAnnotatedFactoryMethod(aString, aPrimitive);
        }

        private ObjectWithJsonbCreatorAnnotatedFactoryMethod(String string, long primitiv) {
            this.string = string;
            this.primitive = primitiv;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithJsonbCreatorAnnotatedFactoryMethod [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithTwoJsonbCreatorAnnotatedSpots implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithJsonbCreatorAnnotatedConstructor("", Long.valueOf(0));
        }

        @JsonbCreator
        public static final ObjectWithTwoJsonbCreatorAnnotatedSpots create( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            return new ObjectWithTwoJsonbCreatorAnnotatedSpots(aString, aPrimitive);
        }

        @JsonbCreator
        public ObjectWithTwoJsonbCreatorAnnotatedSpots( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithTwoJsonbCreatorAnnotatedSpots [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithConstructorPropertiesAnnotation("  ", Long.valueOf(-12));
        }

        @ConstructorProperties({ "string", "primitive" })
        public ObjectWithConstructorPropertiesAnnotation(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithConstructorPropertiesAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithTwoConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithTwoConstructorPropertiesAnnotation("  ", Long.valueOf(-12));
        }

        @ConstructorProperties({ "string" })
        public ObjectWithTwoConstructorPropertiesAnnotation(String aString) {
            this(aString, 0L);
        }

        @ConstructorProperties({ "string", "primitive" })
        public ObjectWithTwoConstructorPropertiesAnnotation(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithTwoConstructorPropertiesAnnotation [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation("", Long.valueOf(0));
        }

        @JsonbCreator
        public static final ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation create( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            return new ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation(aString, aPrimitive);
        }

        @ConstructorProperties({ "string", "primitive" })
        public ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithPublicNoArgAndAnnotatedPrivateConstructor implements ProvidesParameterRepresentation {
        private String string;
        private Long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithPublicNoArgAndAnnotatedPrivateConstructor("  ", Long.valueOf(-12));
        }

        public ObjectWithPublicNoArgAndAnnotatedPrivateConstructor() {
            super();
        }

        @ConstructorProperties({ "string", "primitive" })
        private ObjectWithPublicNoArgAndAnnotatedPrivateConstructor(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        public Long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Long primitive) {
            this.primitive = primitive;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithPublicNoArgAndAnnotatedPrivateConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor implements ProvidesParameterRepresentation {
        private String string;
        private Long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor("  ", Long.valueOf(-12));
        }

        public static final ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor create(String aString, long aPrimitive) {
            return new ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor(aString, aPrimitive);
        }

        public ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor() {
            super();
        }

        @ConstructorProperties({ "string", "primitive" })
        ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        public Long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Long primitive) {
            this.primitive = primitive;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithPublicNoArgAndAnnotatedProtectedConstructor implements ProvidesParameterRepresentation {
        private String string;
        private Long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor("  ", Long.valueOf(-12));
        }

        public static final ObjectWithPublicNoArgAndAnnotatedProtectedConstructor create(String aString, long aPrimitive) {
            return new ObjectWithPublicNoArgAndAnnotatedProtectedConstructor(aString, aPrimitive);
        }

        @ConstructorProperties({ "string", "primitive" })
        protected ObjectWithPublicNoArgAndAnnotatedProtectedConstructor(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        public ObjectWithPublicNoArgAndAnnotatedProtectedConstructor() {
            super();
        }

        public Long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Long primitive) {
            this.primitive = primitive;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ObjectWithMissingConstructorAnnotation implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ObjectWithMissingConstructorAnnotation("a string", Long.MAX_VALUE);
        }

        public ObjectWithMissingConstructorAnnotation(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ObjectWithMissingConstructorAnnotation [string=" + string + ", primitive=" + primitive + "]";
        }
    }
}
