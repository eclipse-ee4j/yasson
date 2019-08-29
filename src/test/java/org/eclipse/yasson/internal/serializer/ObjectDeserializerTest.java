package org.eclipse.yasson.internal.serializer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class ObjectDeserializerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetInstanceExceptionShouldContainClassNameOnMissingConstructor() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("org.eclipse.yasson.internal.serializer.ObjectDeserializerTest$DummyDeserializationClass");

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
