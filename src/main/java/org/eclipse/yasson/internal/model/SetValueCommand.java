package org.eclipse.yasson.internal.model;

import java.lang.reflect.InvocationTargetException;

/**
 * Wrapper for setting a value on javabean property.
 */
@FunctionalInterface
interface SetValueCommand {

    /**
     * Sets a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method setter}.
     *
     * @param object object to invoke set value on, not null.
     * @param value object to be set, nullable.
     * @throws IllegalAccessException if reflection fails.
     * @throws InvocationTargetException if reflection fails.
     */
    void setValue(Object object, Object value) throws IllegalAccessException, InvocationTargetException;
}
