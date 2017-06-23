/*******************************************************************************
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

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
