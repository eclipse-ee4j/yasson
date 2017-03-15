package org.eclipse.yasson.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Sets value with field.
 *
 * @author Roman Grigoriadi
 */
public class SetWithField extends SetValueCommand {

    private final Field field;

    /**
     * Create instance.
     *
     * @param field not null
     */
    public SetWithField(Field field) {
        Objects.requireNonNull(field);
        this.field = field;
    }

    @Override
    public void internalSetValue(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        field.set(object, value);
    }
}
