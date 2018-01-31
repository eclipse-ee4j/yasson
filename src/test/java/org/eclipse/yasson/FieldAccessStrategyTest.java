/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/package org.eclipse.yasson;

import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldAccessStrategyTest {

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
        public String strField;
    }


    @Test
    public void testPrivateFields() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(new FieldAccessStrategy()));

        PrivateFields pojo = new PrivateFields("pojo string");

        String expected = "{\"strField\":\"pojo string\"}";

        Assert.assertEquals(expected, jsonb.toJson(pojo));
        PrivateFields result = jsonb.fromJson(expected, PrivateFields.class);
        Assert.assertEquals(false, result.getterCalled);
        Assert.assertEquals(false, result.setterCalled);
        Assert.assertEquals("pojo string", result.strField);
    }


    @Test
    public void testHidePublicFields() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(new NoAccessStrategy()));

        PublicFields pojo = new PublicFields();
        pojo.strField = "string field";

        String expected = "{}";

        Assert.assertEquals(expected, jsonb.toJson(pojo));
        PublicFields result = jsonb.fromJson("{\"strField\":\"pojo string\"}", PublicFields.class);
        Assert.assertEquals(null, result.strField);
    }

    /**
     * Ignores public / private, visibility is set by name.
     */
    @Test
    public void testCustomVisibityStrategy() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(new CustomVisibilityStrategy()));

        String json = "{\"floatInstance\":10.0,\"stringInstance\":\"Test String\"}";
        SimpleContainer simpleContainer = new SimpleContainer();
        simpleContainer.setStringInstance("Test String");
        simpleContainer.setIntegerInstance(10);
        simpleContainer.setFloatInstance(10.0f);
        Assert.assertEquals(json, jsonb.toJson(simpleContainer));


        SimpleContainer result = jsonb.fromJson("{ \"stringInstance\" : \"Test String\", \"floatInstance\" : 1.0, \"integerInstance\" : 1 }", SimpleContainer.class);
        Assert.assertEquals("Test String", result.stringInstance);
        Assert.assertNull(result.integerInstance);
        Assert.assertNull(result.floatInstance);
    }

    public class CustomVisibilityStrategy implements PropertyVisibilityStrategy {
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
