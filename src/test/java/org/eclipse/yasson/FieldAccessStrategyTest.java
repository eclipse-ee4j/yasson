package org.eclipse.yasson;

import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbTransient;

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
}
