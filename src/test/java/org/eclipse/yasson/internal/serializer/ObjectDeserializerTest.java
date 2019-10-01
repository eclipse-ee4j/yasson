package org.eclipse.yasson.internal.serializer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import javax.json.bind.JsonbException;

public class ObjectDeserializerTest {

    @Test
    public void testGetInstanceExceptionShouldContainClassNameOnMissingConstructor() {
    	assertThrows(JsonbException.class, 
    				() -> defaultJsonb.fromJson("{\"key\":\"value\"}", DummyDeserializationClass.class),
    				DummyDeserializationClass.class::getName);
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
