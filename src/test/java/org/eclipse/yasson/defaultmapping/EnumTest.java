package org.eclipse.yasson.defaultmapping;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.collections.Language;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;

public class EnumTest {

    @Test
    public void testEnumValue() {
        assertEquals("\"Russian\"", defaultJsonb.toJson(Language.Russian));
        Language result = defaultJsonb.fromJson("\"Russian\"", Language.class);
        assertEquals(Language.Russian, result);
    }

    @Test
    public void testEnumInObject() {
        assertEquals("{\"value\":\"Russian\"}", defaultJsonb.toJson(new ScalarValueWrapper<>(Language.Russian)));
        ScalarValueWrapper<Language> result = defaultJsonb.fromJson("{\"value\":\"Russian\"}", new TestTypeToken<ScalarValueWrapper<Language>>() {
        }.getType());

        assertEquals(result.getValue(), Language.Russian);
    }

    @Test
    public void testEnumValueWithToStringOverriden() {
        assertEquals("\"HARD_BACK\"", defaultJsonb.toJson(Binding.HARD_BACK));
        Binding result = defaultJsonb.fromJson("\"HARD_BACK\"", Binding.class);
        assertEquals(Binding.HARD_BACK, result);
    }

    @Test
    public void testEnumInObjectWithToStringOverriden() {
        assertEquals("{\"value\":\"HARD_BACK\"}", defaultJsonb.toJson(new ScalarValueWrapper<>(Binding.HARD_BACK)));
        ScalarValueWrapper<Binding> result = defaultJsonb.fromJson("{\"value\":\"HARD_BACK\"}", new TestTypeToken<ScalarValueWrapper<Binding>>(){}.getType());
        assertEquals(Binding.HARD_BACK, result.getValue());
    }

    public enum Binding {
        HARD_BACK {
            @Override
			public String toString() {
                return "Hard Back";
            }
        }
    }
}
