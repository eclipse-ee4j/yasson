package org.eclipse.yasson.internal;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertCreatedInstanceContainsAllParameters;
import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertParameters;

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedFactoryMethod;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithoutAnnotatedConstructor;
import org.eclipse.yasson.internal.model.JsonbCreator;

import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;

/**
 * Tests the {@link AnnotationIntrospector} with missing optional module "java.deskop", <br>
 * that contains the ConstructorProperties-Annotation.
 * <p>
 * Requires --limit-modules java.base,java.logging,java.sql (to exclude java.desktop) to work. <br>
 * See pom.xml surefire plugin configuration.
 * 
 * @see AnnotationIntrospectorTestFixtures
 */
public class AnnotationIntrospectorWithoutOptionalModulesTest {

    /**
     * class under test.
     */
    private static final AnnotationIntrospector instrospector = new AnnotationIntrospector(new JsonbContext(new JsonbConfig(), JsonProvider.provider()));

    @Test
    public void testNoConstructorPropertiesAnnotationWithoutOptionalModules() {
        String className = "java.beans.ConstructorProperties";
        try {
            Class.forName(className);
            fail("Class [" + className + "] should not be available for this test.");
        } catch (ClassNotFoundException e) {
            // OK, as expected
        }
    }

    @Test
    public void testCreatorShouldBeNullOnMissingConstructorAnnotation() {
        assertNull(instrospector.getCreator(ObjectWithoutAnnotatedConstructor.class));
    }

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedConstructorWithoutOptionalModules() {
        JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAnnotatedConstructor.class);
        assertParameters(ObjectWithJsonbCreatorAnnotatedConstructor.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithJsonbCreatorAnnotatedConstructor.example(), creator);
    }

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedStaticFactoryMethodWithoutOptionalModules() {
        JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAnnotatedFactoryMethod.class);
        assertParameters(ObjectWithJsonbCreatorAnnotatedFactoryMethod.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithJsonbCreatorAnnotatedFactoryMethod.example(), creator);
    }
}