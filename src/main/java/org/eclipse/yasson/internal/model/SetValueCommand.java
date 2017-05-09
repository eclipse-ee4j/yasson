package org.eclipse.yasson.internal.model;

import javax.json.bind.JsonbException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Wrapper for setting a value on javabean property.
 *
 * @author Roman Grigoriadi
 */
abstract class SetValueCommand {

    abstract void internalSetValue(Object object, Object value) throws IllegalAccessException, InvocationTargetException;

    /**
     * Sets a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method setter}.
     *
     * @param object object to invoke set value on, not null.
     * @param value object to be set, nullable.
     * @throws JsonbException if reflection fails.
     */
    final void setValue(Object object, Object value) {
        Objects.requireNonNull(object);
        try {
            internalSetValue(object, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new JsonbException("Error getting value on: " + object, e);
        }
    }
}
