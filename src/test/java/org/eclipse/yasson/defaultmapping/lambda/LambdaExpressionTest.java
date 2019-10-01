package org.eclipse.yasson.defaultmapping.lambda;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

/**
 * Test marshalling objects generated using lambda expressions.
 *
 * Scenarios test lambda-generated objects serialization against concrete class objects serialization. It is thus
 * assumed that concrete class behaviour is tested successfully elsewhere.
 */
public class LambdaExpressionTest {

    private Jsonb jsonb;

    @Before
    public void before() {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testMarshallFunctionalInterface() {
        String name = "WALL-E";
        Addressable control = new Robot(name);
        Addressable lambda = () -> name;
        assertEquals(jsonb.toJson(control), jsonb.toJson(lambda));
    }

    @Test
    public void testMarshallFunctionalInterfaceWithDefaultProperties() {
        String name = "Cheshire";
        Pet control = new Cat(name);
        Pet lambda = () -> name;
        assertEquals(jsonb.toJson(control), jsonb.toJson(lambda));
    }
}
