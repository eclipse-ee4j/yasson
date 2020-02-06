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

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Disabled
class AnnotationFinderTestFixtures {

    public static final String TESTVALUE = "testvalue";

    public static final Annotation[] getMethodAnnotationsOf(Class<?> clazz) {
        try {
            return clazz.getMethod("annotatedMethod").getAnnotations();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public static final Annotation[] getConstructorAnnotationsOf(Class<?> clazz) {
        try {
            return clazz.getConstructor(String.class).getAnnotations();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public static class ObjectWithNoAnnotations {
        public void annotatedMethod() {
            // empty
        }
    }

    public static class ObjectWithDeprecatedMethod {
        @Deprecated
        public void annotatedMethod() {
            // empty
        }
    }

    public static class ObjectWithIgnoredMethod {
        @Disabled
        public void annotatedMethod() {
            // empty
        }
    }

    public static class ObjectWithDeprecatedAndIgnoredMethod {
    	@Disabled
        @Deprecated
        public void annotatedMethod() {
            // empty
        }
    }

    public static class ObjectWithInheritedDeprecatedMethod {
        @AnnotationAnnotatedWithDeprecated()
        public void annotatedMethod() {
            // empty
        }
    }

    public static class ObjectWithIgnoredAndInheritedDeprecatedMethod {
    	@Disabled
        @AnnotationAnnotatedWithDeprecated
        public void annotatedMethod() {
            // empty
        }
    }

    public static class ObjectWithInheritedAndDirectlyDeprecatedMethod {
        @Deprecated
        @AnnotationAnnotatedWithDeprecated
        public void annotatedMethod() {
            // empty
        }
    }

    public static class ObjectWithConstructAnnotation {
        @AnnotationAnnotatedWithDeprecated
        @ConstructorProperties({ TESTVALUE })
        public ObjectWithConstructAnnotation(String testvalue) {
            // empty
        }
    }

    public static class ObjectWithMissingValuePropertyAnnotation {
        @AnnotationWithoutValueProperty
        public void annotatedMethod() {
            // empty
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = { METHOD, CONSTRUCTOR, TYPE })
    @Deprecated(since = "inherited")
    public @interface AnnotationAnnotatedWithDeprecated {

        String value() default TESTVALUE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = { METHOD, CONSTRUCTOR, TYPE })
    public @interface AnnotationWithoutValueProperty {

        String someOtherProperty() default TESTVALUE;
    }
}
