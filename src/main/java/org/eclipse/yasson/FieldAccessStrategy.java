package org.eclipse.yasson;

import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>Strategy that can be used to force always using fields instead of getters setters for getting / setting value.</p>
 *
 * <p>Suggested approach is to use default visibility strategy, which will use public getters / setters, or field
 * if it is public.</p>
 *
 * <p>Please consider, that forcing accessing fields will in most cases (when field is not public)
 * result in calling {@link Field#setAccessible(boolean)} to break into clients code.
 * This may cause problems if client code is loaded as JPMS (Java Platform Module System) module, as OSGi module or
 * when SecurityManager is turned on.</p>
 */
public class FieldAccessStrategy implements PropertyVisibilityStrategy {
    @Override
    public boolean isVisible(Field field) {
        return true;
    }

    @Override
    public boolean isVisible(Method method) {
        return false;
    }
}
