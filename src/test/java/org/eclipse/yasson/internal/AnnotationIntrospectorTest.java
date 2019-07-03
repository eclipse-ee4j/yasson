package org.eclipse.yasson.internal;

import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertCreatedInstanceContainsAllParameters;
import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertParameters;
import static org.junit.Assert.assertNull;

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.JsonbCreatorAndConstructorPropertiesAnnotation;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.JsonbCreatorAnnotatedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.JsonbCreatorAnnotatedFactoryMethod;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.JsonbCreatorAnnotatedProtectedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.MissingConstructorAnnotation;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.TwoJsonbCreatorAnnotatedSpots;
import org.eclipse.yasson.internal.model.JsonbCreator;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;

import javax.json.spi.JsonProvider;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests the {@link AnnotationIntrospector}.
 * 
 * @see AnnotationIntrospectorTestFixtures
 * @see AnnotationIntrospectorTestAsserts
 */
public class AnnotationIntrospectorTest {

    private JsonbContext jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());

    /**
     * class under test.
     */
    private AnnotationIntrospector instrospector = new AnnotationIntrospector(jsonbContext);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedConstructor() {
        JsonbCreator creator = instrospector.getCreator(JsonbCreatorAnnotatedConstructor.class);
        assertParameters(JsonbCreatorAnnotatedConstructor.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(JsonbCreatorAnnotatedConstructor.example(), creator);
    }

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedStaticFactoryMethod() {
        JsonbCreator creator = instrospector.getCreator(JsonbCreatorAnnotatedFactoryMethod.class);
        assertParameters(JsonbCreatorAnnotatedFactoryMethod.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(JsonbCreatorAnnotatedFactoryMethod.example(), creator);
    }

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedStaticFactoryMethodIgnoringConstructorProperties() {
        JsonbCreator creator = instrospector.getCreator(JsonbCreatorAndConstructorPropertiesAnnotation.class);
        assertParameters(JsonbCreatorAndConstructorPropertiesAnnotation.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(JsonbCreatorAndConstructorPropertiesAnnotation.example(), creator);
    }

    @Test
    public void testJsonbAnnotatedProtectedConstructorLeadsToAnException() {
        exception.expect(JsonbException.class);
        exception.expectCause(IsInstanceOf.instanceOf(IllegalAccessException.class));
        JsonbCreator creator = instrospector.getCreator(JsonbCreatorAnnotatedProtectedConstructor.class);
        assertCreatedInstanceContainsAllParameters(JsonbCreatorAnnotatedProtectedConstructor.example(), creator);
    }

    @Test
    public void testMoreThanOneAnnotatedCreatorMethodShouldLeadToAnException() {
        exception.expect(JsonbException.class);
        exception.expectMessage("More than one @" + JsonbCreator.class.getSimpleName());
        instrospector.getCreator(TwoJsonbCreatorAnnotatedSpots.class);
    }

    @Test
    public void testCreatorShouldBeNullOnMissingConstructorAnnotation() {
        assertNull(instrospector.getCreator(MissingConstructorAnnotation.class));
    }

}