
package org.eclipse.yasson.internal.model.customization.visibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.json.bind.config.PropertyVisibilityStrategy;

/**
 *
 * @author Adam Bien
 */
public class PrivateFieldVisibilityStrategy implements PropertyVisibilityStrategy {

    @Override
    public boolean isVisible(Field field) {
        return true;
    }

    @Override
    public boolean isVisible(Method method) {
        return false;
    }

}
