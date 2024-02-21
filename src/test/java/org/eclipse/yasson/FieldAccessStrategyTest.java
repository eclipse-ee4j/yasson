/*
 * Copyright (c) 2016, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.yasson;

 import org.junit.jupiter.api.*;

 import static org.eclipse.yasson.Jsonbs.testWithJsonbBuilderCreate;
 import static org.junit.jupiter.api.Assertions.*;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class FieldAccessStrategyTest {

    private FieldAccessStrategyTest() {
    }

    public static class PrivateFields {
        private String strField;
        @JsonbTransient
        private boolean setterCalled;
        @JsonbTransient
        private boolean getterCalled;

        public PrivateFields() {
        }

        public PrivateFields(String strField) {
            this.strField = strField;
        }

        public String getStrField() {
            getterCalled = true;
            return strField;
        }

        public void setStrField(String strField) {
            setterCalled = true;
            this.strField = strField;
        }
    }

    public static class PublicFields {

        protected PublicFields() {
        }

        public String strField;
    }


    @Test
    public void testPrivateFields() {
        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyVisibilityStrategy(new FieldAccessStrategy()), jsonb -> {

            PrivateFields pojo = new PrivateFields("pojo string");

            String expected = "{\"strField\":\"pojo string\"}";

            assertEquals(expected, jsonb.toJson(pojo));
            PrivateFields result = jsonb.fromJson(expected, PrivateFields.class);
            assertFalse(result.getterCalled);
            assertFalse(result.setterCalled);
            assertEquals("pojo string", result.strField);
        });
    }


    @Test
    public void testHidePublicFields() {
        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyVisibilityStrategy(new NoAccessStrategy()), jsonb -> {

            PublicFields pojo = new PublicFields();
            pojo.strField = "string field";

            String expected = "{}";

            assertEquals(expected, jsonb.toJson(pojo));
            PublicFields result = jsonb.fromJson("{\"strField\":\"pojo string\"}", PublicFields.class);
            assertNull(result.strField);
        });
    }

    /**
     * Ignores public / private, visibility is set by name.
     */
    @Test
    public void testCustomVisibityStrategy() {
        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyVisibilityStrategy(new CustomVisibilityStrategy()), jsonb -> {

            String json = "{\"floatInstance\":10.0,\"stringInstance\":\"Test String\"}";
            SimpleContainer simpleContainer = new SimpleContainer();
            simpleContainer.setStringInstance("Test String");
            simpleContainer.setIntegerInstance(10);
            simpleContainer.setFloatInstance(10.0f);
            assertEquals(json, jsonb.toJson(simpleContainer));


            SimpleContainer result = jsonb.fromJson("{ \"stringInstance\" : \"Test String\", \"floatInstance\" : 1.0, \"integerInstance\" : 1 }",
                    SimpleContainer.class);
            assertEquals("Test String", result.stringInstance);
            assertNull(result.integerInstance);
            assertNull(result.floatInstance);
        });
    }

    public static class CustomVisibilityStrategy implements PropertyVisibilityStrategy {
        public CustomVisibilityStrategy() {
        }

        @Override
        public boolean isVisible(Field field) {
            return field.getName().equals("stringInstance");
        }

        @Override
        public boolean isVisible(Method method) {
            return method.getName().equals("getFloatInstance");
        }
    }

    public static class SimpleContainer {
        protected SimpleContainer() {
        }

        private String stringInstance;
        private Integer integerInstance;
        private Float floatInstance;

        public String getStringInstance() {
            return stringInstance;
        }

        public void setStringInstance(String stringInstance) {
            this.stringInstance = stringInstance;
        }

        public Integer getIntegerInstance() {
            return integerInstance;
        }

        public void setIntegerInstance(Integer integerInstance) {
            this.integerInstance = integerInstance;
        }

        public float getFloatInstance() {
            return floatInstance;
        }

        public void setFloatInstance(float floatInstance) {
            this.floatInstance = floatInstance;
        }
    }


    private static final class NoAccessStrategy implements PropertyVisibilityStrategy {

        public NoAccessStrategy() {
        }

        @Override
        public boolean isVisible(Field field) {
            return false;
        }

        @Override
        public boolean isVisible(Method method) {
            return false;
        }
    }
}
