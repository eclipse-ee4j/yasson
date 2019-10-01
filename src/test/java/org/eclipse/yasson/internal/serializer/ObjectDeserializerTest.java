package org.eclipse.yasson.internal.serializer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

public class ObjectDeserializerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetInstanceExceptionShouldContainClassNameOnMissingConstructor() {
        expectedException.expect(JsonbException.class);
        expectedException.expectMessage(DummyDeserializationClass.class.getName());

        Jsonb jsonb = JsonbBuilder.create();
        jsonb.fromJson("{\"key\":\"value\"}", DummyDeserializationClass.class);
    }

    public static class DummyDeserializationClass {
        private String key;

        public DummyDeserializationClass(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
