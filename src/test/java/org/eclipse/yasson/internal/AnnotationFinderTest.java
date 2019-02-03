package org.eclipse.yasson.internal;

import static org.eclipse.yasson.internal.AnnotationFinderTestFixtures.getMethodAnnotationsOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.yasson.internal.AnnotationFinderTestFixtures.ObjectWithDeprecatedAndIgnoredMethod;
import org.eclipse.yasson.internal.AnnotationFinderTestFixtures.ObjectWithDeprecatedMethod;
import org.eclipse.yasson.internal.AnnotationFinderTestFixtures.ObjectWithIgnoredAndInheritedDeprecatedMethod;
import org.eclipse.yasson.internal.AnnotationFinderTestFixtures.ObjectWithIgnoredMethod;
import org.eclipse.yasson.internal.AnnotationFinderTestFixtures.ObjectWithInheritedAndDirectlyDeprecatedMethod;
import org.eclipse.yasson.internal.AnnotationFinderTestFixtures.ObjectWithInheritedDeprecatedMethod;
import org.eclipse.yasson.internal.AnnotationFinderTestFixtures.ObjectWithNoAnnotations;

import org.junit.Test;

public class AnnotationFinderTest {

    /**
     * class under test.
     */
    private AnnotationFinder<Deprecated> findAnnotation = AnnotationFinder.findAnnotation(Deprecated.class);

    @Test
    public void testAnnotationShouldBeFound() {
	assertNotNull(findAnnotation.in(getMethodAnnotationsOf(ObjectWithDeprecatedMethod.class)));
    }

    @Test
    public void testAnnotationShouldBeFoundWithinOthers() {
	assertNotNull(findAnnotation.in(getMethodAnnotationsOf(ObjectWithDeprecatedAndIgnoredMethod.class)));
    }

    @Test
    public void testInheritedAnnotationShouldBeFound() {
	Deprecated annotation = findAnnotation.in(getMethodAnnotationsOf(ObjectWithInheritedDeprecatedMethod.class));
	assertEquals("inherited", annotation.since());
    }

    @Test
    public void testInheritedAnnotationShouldBeFoundWithinOthers() {
	Deprecated annotation = findAnnotation.in(getMethodAnnotationsOf(ObjectWithIgnoredAndInheritedDeprecatedMethod.class));
	assertEquals("inherited", annotation.since());
    }

    @Test
    public void testDirectAnnotationShouldBePreferedOverInheritedOnes() {
	Deprecated annotation = findAnnotation.in(getMethodAnnotationsOf(ObjectWithInheritedAndDirectlyDeprecatedMethod.class));
	assertEquals("", annotation.since());
    }

    @Test
    public void testResultShouldBeNullWhenThereIsNoAnnotation() {
	assertNull(findAnnotation.in(getMethodAnnotationsOf(ObjectWithNoAnnotations.class)));
    }

    @Test
    public void testResultShouldBeNullWhenThereAreOnlyOtherAnnotations() {
	assertNull(findAnnotation.in(getMethodAnnotationsOf(ObjectWithIgnoredMethod.class)));
    }
}