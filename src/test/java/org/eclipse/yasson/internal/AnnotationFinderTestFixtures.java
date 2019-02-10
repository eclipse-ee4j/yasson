package org.eclipse.yasson.internal;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Ignore;

@Ignore
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
        @Ignore
        public void annotatedMethod() {
            // empty
        }
    }

    public static class ObjectWithDeprecatedAndIgnoredMethod {
        @Ignore
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
        @Ignore
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