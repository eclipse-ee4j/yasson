package org.eclipse.yasson.defaultmapping.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbTransient;

import org.jboss.weld.exceptions.IllegalStateException;
import org.junit.Test;

/**
 * Tests to verify that read-only properties (properties with no field or setter)
 * are ignored when deserializing and that write-only properties (properties with
 * no field or getter) are ignored when serializing.
 */
public class PropertyMismatchTest {

    public static class CollectionGetterOnly {
        public HiddenCtorCollection getDataCollection() {
            throw new IllegalStateException("Get was called");
        }

        public HiddenCtorMap getDataMap() {
            throw new IllegalStateException("Get was called");
        }

        public HiddenCtorType[] getDataArray() {
            throw new IllegalStateException("Get was called");
        }

        public HiddenCtorType getData() {
            throw new IllegalStateException("Get was called");
        }
    }

    public static class CollectionSetterOnly {
        public void setDataCollection(HiddenCtorCollection set) {
            throw new IllegalStateException("Set was called");
        }

        public void setDataMap(HiddenCtorMap map) {
            throw new IllegalStateException("Set was called");
        }

        public void setDataArray(HiddenCtorType[] arr) {
            throw new IllegalStateException("Set was called");
        }

        public void setData(HiddenCtorType obj) {
            throw new IllegalStateException("Set was called");
        }
    }

    public static class HiddenCtorCollection extends HashSet<String> {
        private static final long serialVersionUID = -2254550505591024068L;

        private HiddenCtorCollection() {
            throw new IllegalStateException("Object should not be initialized!");
        }
    }

    public static class HiddenCtorMap extends HashMap<String,String> {
        private static final long serialVersionUID = -3042588327575185446L;

        private HiddenCtorMap() {
            throw new IllegalStateException("Object should not be initialized!");
        }
    }

    public static class HiddenCtorType {

        private HiddenCtorType() {
            throw new IllegalStateException("Object should not be initialized!");
        }
    }

    /**
     * When deserializing a JSON, a property without a setter should be ignored as a property
     */
    @Test
    public void testGetterOnly() {
        String jsonCollection = "{\"dataCollection\": [\"foo\"], " +
                                 "\"dataMap\": { \"foo\": \"bar\" }, " +
                                 "\"dataArray\": [\"foo\"], " +
                                 "\"data\": \"foo\" }";
        CollectionGetterOnly collection = JsonbBuilder.create().fromJson(jsonCollection, CollectionGetterOnly.class);
        // Don't need to verify resulting object (except that it is non-null)
        // because if any getters or ctors were called, we would get an ISE
        assertNotNull(collection);
    }

    /**
     * When serializing an object, a property without a getter should be ignored as a property
     */
    @Test
    public void testSetterOnly() {
        CollectionSetterOnly obj = new CollectionSetterOnly();
        String json = JsonbBuilder.create().toJson(obj);
        assertEquals("{}", json);
    }
    
    public static class PropertyTypeMismatch {
        @JsonbTransient
        public Instant internalInstantProperty;
        
        private String foo;
        public int getFoo() {
            return foo.length();
        }
        public void setFoo(Instant instant) {
            this.foo = instant.toString();
            this.internalInstantProperty = instant;
        }
    }
    
    /**
     * Test that properties of the same name with different
     * field/getter/setter types behave properly and that we don't
     * assume they are all equal
     */
    @Test
    public void testPropertyTypesMismatch() {
        PropertyTypeMismatch obj = new PropertyTypeMismatch();
        Instant now = Instant.now();
        obj.setFoo(now);
        
        Jsonb jsonb = JsonbBuilder.create();
        String json = jsonb.toJson(obj);
        assertEquals("{\"foo\":" + now.toString().length() + "}", json);
        
        PropertyTypeMismatch after = jsonb.fromJson("{\"foo\":\"" + now.toString() + "\"}", PropertyTypeMismatch.class);
        assertEquals(obj.getFoo(), after.getFoo());
        assertEquals(obj.internalInstantProperty, after.internalInstantProperty);
    }
}
