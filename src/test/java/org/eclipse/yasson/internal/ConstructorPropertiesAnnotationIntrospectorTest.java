package org.eclipse.yasson.internal;

import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertCreatedInstanceContainsAllParameters;
import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertParameters;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithConstructorPropertiesAnnotation;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithTwoConstructorPropertiesAnnotation;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithoutAnnotatedConstructor;
import org.eclipse.yasson.internal.model.JsonbCreator;

import javax.json.bind.JsonbConfig;

import java.beans.ConstructorProperties;

import javax.json.spi.JsonProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConstructorPropertiesAnnotationIntrospectorTest {

    private JsonbContext jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());
    private AnnotationFinder<ConstructorProperties> constructorPropertiesFinder = AnnotationFinder.findAnnotation(ConstructorProperties.class);

    /**
     * class under test.
     */
    private ConstructorPropertiesAnnotationIntrospector instrospector = new ConstructorPropertiesAnnotationIntrospector(jsonbContext, constructorPropertiesFinder);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testObjectShouldBeCreateableFromConstructorPropertiesAnnotatedConstructor() {
	JsonbCreator creator = instrospector.getCreator(ObjectWithConstructorPropertiesAnnotation.class);
	assertParameters(ObjectWithConstructorPropertiesAnnotation.parameters(), creator);
	assertCreatedInstanceContainsAllParameters(ObjectWithConstructorPropertiesAnnotation.example(), creator);
    }

    @Test
    public void testShouldAlsoWorkWithStaticFactoryMethodAndPredefinedAnnotationFinder() {
	instrospector = ConstructorPropertiesAnnotationIntrospector.forContext(jsonbContext);
	JsonbCreator creator = instrospector.getCreator(ObjectWithConstructorPropertiesAnnotation.class);
	assertNotNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereIsNoCreatorAnnotation() {
	JsonbCreator creator = instrospector.getCreator(ObjectWithoutAnnotatedConstructor.class);
	assertNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereIsNoConstructorPropertiesAnnotation() {
	JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAnnotatedConstructor.class);
	assertNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereAreMoreThanOneConstructorPropertiesAnnotation() {
	JsonbCreator creator = instrospector.getCreator(ObjectWithTwoConstructorPropertiesAnnotation.class);
	assertNull(creator);
    }

}