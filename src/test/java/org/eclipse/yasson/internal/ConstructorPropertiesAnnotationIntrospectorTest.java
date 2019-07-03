package org.eclipse.yasson.internal;

import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertCreatedInstanceContainsAllParameters;
import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertParameters;
import static org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.constructorsOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ConstructorPropertiesAnnotation;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.JsonbCreatorAnnotatedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.MissingAnnotationConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.PublicNoArgAndAnnotatedPackageProtectedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.PublicNoArgAndAnnotatedPrivateConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.PublicNoArgAndAnnotatedProtectedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.TwoConstructorPropertiesAnnotation;
import org.eclipse.yasson.internal.model.JsonbCreator;

import javax.json.bind.JsonbConfig;

import javax.json.spi.JsonProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConstructorPropertiesAnnotationIntrospectorTest {

    private JsonbContext jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());
    private AnnotationFinder constructorPropertiesFinder = AnnotationFinder.findConstructorProperties();

    /**
     * class under test.
     */
    private ConstructorPropertiesAnnotationIntrospector instrospector = new ConstructorPropertiesAnnotationIntrospector(jsonbContext, constructorPropertiesFinder);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testObjectShouldBeCreateableFromConstructorPropertiesAnnotatedConstructor() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ConstructorPropertiesAnnotation.class));
        assertParameters(ConstructorPropertiesAnnotation.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ConstructorPropertiesAnnotation.example(), creator);
    }

    @Test
    public void testShouldAlsoWorkWithStaticFactoryMethodAndPredefinedAnnotationFinder() {
        instrospector = ConstructorPropertiesAnnotationIntrospector.forContext(jsonbContext);
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ConstructorPropertiesAnnotation.class));
        assertNotNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereIsNoCreatorAnnotation() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(MissingAnnotationConstructor.class));
        assertNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereIsNoConstructorPropertiesAnnotation() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(JsonbCreatorAnnotatedConstructor.class));
        assertNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereAreMoreThanOneConstructorPropertiesAnnotation() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(TwoConstructorPropertiesAnnotation.class));
        assertNull(creator);
    }

    @Test
    public void testAnnotatedInaccessiblePrivateConstructorShouldBeIgnored() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(PublicNoArgAndAnnotatedPrivateConstructor.class));
        assertNull(creator);
    }

    @Test
    public void testAnnotatedInaccessiblePackageProtectedConstructorShouldBeIgnored() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(PublicNoArgAndAnnotatedPackageProtectedConstructor.class));
        assertNull(creator);
    }

    @Test
    public void testAnnotatedInaccessibleProtectedConstructorShouldBeIgnored() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(PublicNoArgAndAnnotatedProtectedConstructor.class));
        assertNull(creator);
    }
}