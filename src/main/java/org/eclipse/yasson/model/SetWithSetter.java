package org.eclipse.yasson.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Sets a value with a setter.
 *
 * @author Roman Grigoriadi
 */
public class SetWithSetter extends SetValueCommand {

    private final Method method;

    /**
     * Create instance
     * @param method not null
     */
    public SetWithSetter(Method method) {
        Objects.requireNonNull(method);
        this.method = method;
    }

    @Override
    void internalSetValue(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        method.invoke(object, value);
    }
}
