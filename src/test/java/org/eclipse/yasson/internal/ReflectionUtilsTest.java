package org.eclipse.yasson.internal;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Roman Grigoriadi
 */
public class ReflectionUtilsTest {

    public static class Types<T> {

        public List<String> resolvedParameterizedField;

        public List<Map<Integer, String>> resolvedNestedParameterizedField;

        public String resolvedStr;

        public List<T> unresolvedParameterizedField;

        public List<Map<Integer, T>> unresolvedNestedParameterizedField;

        public T unresolvedField;

        public List<?> unresolvedWildcardField;
    }


    @Test
    public void testIsTypeResolved() {
        Types<String> types = new Types<String>();
        assertTrue(ReflectionUtils.isResolvedType(getFieldType("resolvedParameterizedField")));
        assertTrue(ReflectionUtils.isResolvedType(getFieldType("resolvedNestedParameterizedField")));
        assertTrue(ReflectionUtils.isResolvedType(getFieldType("resolvedStr")));
        assertFalse(ReflectionUtils.isResolvedType(getFieldType("unresolvedParameterizedField")));
        assertFalse(ReflectionUtils.isResolvedType(getFieldType("unresolvedNestedParameterizedField")));
        assertFalse(ReflectionUtils.isResolvedType(getFieldType("unresolvedField")));
        assertFalse(ReflectionUtils.isResolvedType(getFieldType("unresolvedWildcardField")));
    }

    private Type getFieldType(String fieldName) {
        try {
            Field field = Types.class.getField(fieldName);
            return field.getGenericType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
