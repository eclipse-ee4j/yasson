package org.eclipse.yasson.model;

import javax.json.bind.JsonbException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Wrapper for getting value from javabean property.
 *
 * @author Roman Grigoriadi
 */
abstract class GetValueCommand {

    /**
     * Get a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method getter}.
     *
     * @param object object to invoke get value on, not null.
     * @throws IllegalAccessException if reflection fails.
     * @throws InvocationTargetException if reflection fails.
     * @return value
     */
    abstract Object internalGetValue(Object object) throws IllegalAccessException, InvocationTargetException;

    /**
     * Get a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method getter}.
     *
     * @param object object to invoke get value on, not null.
     * @throws JsonbException if reflection fails.
     * @return value
     */
    final Object getValue(Object object) {
        Objects.requireNonNull(object);
        try {
            return internalGetValue(object);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new JsonbException("Error getting value on: " + object, e);
        }
    }
}
