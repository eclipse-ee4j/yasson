package org.eclipse.yasson.model;

import java.lang.reflect.Type;

/**
 * Parameter for creator constructor / method.
 *
 * @author Roman Grigoriadi
 */
public class CreatorParam {

    private final String name;

    private final Type type;

    public CreatorParam(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

}
