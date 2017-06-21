package org.eclipse.yasson.internal.components;

import javax.json.bind.JsonbException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Delegate call to {@link JsonbComponentInstanceCreatorFactory} avoiding static imports of CDI classes.
 */
public class InstanceCreatorFactoryHelper {

    public static JsonbComponentInstanceCreator getComponentInstanceCreator() {
        return AccessController.doPrivileged((PrivilegedAction<JsonbComponentInstanceCreator>) () -> {
            try {
                Class<?> cdiClass = Class.forName("javax.enterprise.inject.spi.CDI", false, InstanceCreatorFactoryHelper.class.getClassLoader());
                Class<?> factoryClass = Class.forName("org.eclipse.yasson.internal.components.JsonbComponentInstanceCreatorFactory");
                Method factoryMethod = factoryClass.getMethod("getComponentInstanceCreator");
                return (JsonbComponentInstanceCreator) factoryMethod.invoke(factoryClass);
            } catch (ClassNotFoundException e) {
                return new DefaultConstructorCreator();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new JsonbException("Error loading JsonbComponentInstanceCreator", e);
            }
        });
    }
}
