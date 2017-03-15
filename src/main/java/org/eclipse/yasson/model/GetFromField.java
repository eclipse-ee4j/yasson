package org.eclipse.yasson.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Gets value from field.
 *
 * @author Roman Grigoriadi
 */
public class GetFromField extends GetValueCommand {

    private final Field field;

    /**
     * Create instance.
     *
     * @param field not null
     */
    public GetFromField(Field field) {
        this.field = field;
    }

    @Override
    public Object internalGetValue(Object object) throws IllegalAccessException, InvocationTargetException {
        return field.get(object);
    }
}
