package org.eclipse.yasson.defaultmapping;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.collections.Language;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.assertEquals;

public class EnumTest {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testEnumValue() {
        assertEquals("\"Russian\"", jsonb.toJson(Language.Russian));
        Language result = jsonb.fromJson("\"Russian\"", Language.class);
        Assert.assertEquals(Language.Russian, result);
    }

    @Test
    public void testEnumInObject() {
        Assert.assertEquals("{\"value\":\"Russian\"}", jsonb.toJson(new ScalarValueWrapper<>(Language.Russian)));
        ScalarValueWrapper<Language> result = jsonb.fromJson("{\"value\":\"Russian\"}", new TestTypeToken<ScalarValueWrapper<Language>>() {
        }.getType());

        Assert.assertEquals(result.getValue(), Language.Russian);
    }

    @Test
    public void testEnumValueWithToStringOverriden() {
        assertEquals("\"HARD_BACK\"", jsonb.toJson(Binding.HARD_BACK));
        Binding result = jsonb.fromJson("\"HARD_BACK\"", Binding.class);
        Assert.assertEquals(Binding.HARD_BACK, result);
    }


    @Test
    public void testEnumInObjectWithToStringOverriden() {
        assertEquals("{\"value\":\"HARD_BACK\"}", jsonb.toJson(new ScalarValueWrapper<>(Binding.HARD_BACK)));
        ScalarValueWrapper<Binding> result = jsonb.fromJson("{\"value\":\"HARD_BACK\"}", new TestTypeToken<ScalarValueWrapper<Binding>>(){}.getType());
        Assert.assertEquals(Binding.HARD_BACK, result.getValue());
    }

    public enum Binding {
        HARD_BACK {
            public String toString() {
                return "Hard Back";
            }
        }
    }
}
