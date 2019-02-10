package org.eclipse.yasson.internal;

import static org.junit.Assert.assertNull;

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithMissingConstructorAnnotation;

import javax.json.bind.JsonbConfig;

import javax.json.spi.JsonProvider;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link AnnotationIntrospector} with missing optional module "java.deskop", <br>
 * that contains the ConstructorProperties-Annotation.
 * <p>
 * Requires --limit-modules java.base,java.logging,java.sql (to exclude java.desktop) to work. <br>
 * See pom.xml surefire plugin configuration.
 * 
 * @see AnnotationIntrospectorTestFixtures
 */
public class AnnotationIntrospectorWithoutJavaDektopModuleTest {

    private JsonbContext jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());

    /**
     * class under test.
     */
    private AnnotationIntrospector instrospector = new AnnotationIntrospector(jsonbContext);

    @Test
    public void testNoConstructorPropertiesAnnotationWithoutModuleJavaDesktop() {
        String className = "java.beans.ConstructorProperties";
        try {
            Class.forName(className);
            Assert.fail("Class [" + className + "] should not be available for this test.");
        } catch (ClassNotFoundException e) {
            // OK, as expected
        }
    }

    @Test
    public void testCreatorShouldBeNullOnMissingConstructorAnnotation() {
        assertNull(instrospector.getCreator(ObjectWithMissingConstructorAnnotation.class));
    }
}