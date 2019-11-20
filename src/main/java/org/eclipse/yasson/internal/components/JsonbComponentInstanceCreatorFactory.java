/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.components;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.InstanceCreator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

/**
 * Factory method for default Jsonb component instance creators.
 */
public class JsonbComponentInstanceCreatorFactory {

    private static final Logger LOGGER = Logger.getLogger(JsonbComponentInstanceCreator.class.getName());

    private JsonbComponentInstanceCreatorFactory() {
        throw new IllegalStateException("This class should never be instantiated");
    }

    /**
     * JNDI bean manager name.
     */
    public static final String BEAN_MANAGER_NAME = "java:comp/BeanManager";

    /**
     * Initial context class.
     */
    public static final String INITIAL_CONTEXT_CLASS = "javax.naming.InitialContext";
    private static final String CDI_SPI_CLASS = "javax.enterprise.inject.spi.CDI";

    /**
     * First check a CDI provider, if available use those.
     * Try to lookup in a JNDI if no provider is registered.
     * If one of the above is found {@link BeanManagerInstanceCreator} is returned,
     * or {@link DefaultConstructorCreator} otherwise.
     *
     * @param creator Instance creator
     * @return Component instance creator, either CDI or default constructor.
     */
    public static JsonbComponentInstanceCreator getComponentInstanceCreator(InstanceCreator creator) {
        Object beanManager = getCdiBeanManager();
        if (beanManager == null) {
            beanManager = getJndiBeanManager();
        }
        if (beanManager == null) {
            LOGGER.finest(Messages.getMessage(MessageKeys.BEAN_MANAGER_NOT_FOUND_USING_DEFAULT));
            return new DefaultConstructorCreator(creator);
        }
        return new BeanManagerInstanceCreator(beanManager);
    }

    /**
     * Get bean manager with CDI api.
     *
     * @return bean manager instance or null if CDI API dependency is not available.
     */
    private static Object getCdiBeanManager() {
        return AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                return getBeanManager(() -> {
                    Class<?> cdiClass = Class.forName(CDI_SPI_CLASS);
                    Method current = cdiClass.getMethod("current");
                    Method getBeanManager = cdiClass.getMethod("getBeanManager");
                    Object cdiObject = current.invoke(cdiClass);
                    if (cdiObject == null) {
                        return null;
                    }
                    return getBeanManager.invoke(cdiObject);
                });
            } catch (ClassNotFoundException e) {
                LOGGER.finest(Messages.getMessage(MessageKeys.NO_CDI_API_PROVIDER, CDI_SPI_CLASS));
                return null;
            }
        });
    }

    /**
     * Get bean manager from JNDI context.
     *
     * @return bean manager instance or null if javax.naming is not available.
     */
    private static Object getJndiBeanManager() {
        return AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                return getBeanManager(() -> {
                    Class<?> initialContextClass = Class.forName(INITIAL_CONTEXT_CLASS);
                    Method lookupMethod = initialContextClass.getMethod("lookup", String.class);
                    Constructor<?> initialContextConstructor = initialContextClass.getConstructor();
                    Object initialContextObject = initialContextConstructor.newInstance();
                    return lookupMethod.invoke(initialContextObject, BEAN_MANAGER_NAME);
                });
            } catch (ClassNotFoundException e) {
                LOGGER.finest(Messages.getMessage(MessageKeys.NO_JNDI_ENVIRONMENT, INITIAL_CONTEXT_CLASS));
                return null;
            }
        });
    }

    /**
     * Handles common invocation exceptions for getting bean manager reflectively.
     *
     * @return bean manager instance or null if javax.naming is not available or insufficient permissions.
     */
    private static Object getBeanManager(BeanManagerProvider command) throws ClassNotFoundException {
        try {
            return command.provide();
        } catch (NoSuchMethodException | InstantiationException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, e.getMessage()), e);
        } catch (IllegalAccessException e) {
            //insufficient permissions for reflective invocation, don't fail in this case
            LOGGER.warning(e.getMessage());
            LOGGER.warning(Messages.getMessage(MessageKeys.ILLEGAL_ACCESS, "lookup CDI bean manager"));
            return null;
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (ReflectiveOperationException e) {
            //likely no CDI container is running or bean manager JNDI lookup fails.
            if (e.getCause() != null) {
                LOGGER.finest(e.getMessage());
            }
            LOGGER.finest(Messages.getMessage(MessageKeys.NO_CDI_ENVIRONMENT));
            return null;
        }
    }

    /**
     * Provides CDI bean manager instance, declares all exceptions thrown with reflective calls.
     */
    private interface BeanManagerProvider {
        Object provide() throws ReflectiveOperationException;
    }
}
