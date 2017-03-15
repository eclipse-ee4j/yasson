package org.eclipse.yasson.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Gets value with getter.
 *
 * @author Roman Grigoriadi
 */
public class GetFromGetter extends GetValueCommand {

    private final Method method;

    /**
     * Create instance.
     *
     * @param method not null
     */
    public GetFromGetter(Method method) {
        Objects.requireNonNull(method);
        this.method = method;
    }

    @Override
    Object internalGetValue(Object object) throws IllegalAccessException, InvocationTargetException {
        return method.invoke(object);
    }
}
