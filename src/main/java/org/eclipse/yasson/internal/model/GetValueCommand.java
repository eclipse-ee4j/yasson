package org.eclipse.yasson.internal.model;

import java.lang.reflect.InvocationTargetException;

/**
 * Wrapper for getting value from javabean property.
 */
@FunctionalInterface
interface GetValueCommand {

    /**
     * Get a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method getter}.
     *
     * @param object object to invoke get value on, not null.
     * @return value
     * @throws IllegalAccessException    if reflection fails.
     * @throws InvocationTargetException if reflection fails.
     */
    Object getValue(Object object) throws IllegalAccessException, InvocationTargetException;
}