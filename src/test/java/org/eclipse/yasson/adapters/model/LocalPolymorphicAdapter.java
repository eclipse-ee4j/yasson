/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.adapters.model;

import jakarta.json.bind.adapter.JsonbAdapter;
import java.lang.reflect.Constructor;
import java.util.stream.Stream;

/**
 * Causes {@link StackOverflowError} if recursive calls of user components are not checked by runtime.
 */
public abstract class LocalPolymorphicAdapter<T> implements JsonbAdapter<T, LocalTypeWrapper<T>> {

    private final String[] allowedClasses;

    /**
     * Create new instance.
     *
     * @param allowedClasses allowed classes for loading by name
     */
    public LocalPolymorphicAdapter(final Class... allowedClasses) {
        this.allowedClasses = Stream.of(allowedClasses).map(Class::getName).toArray(value -> new String[allowedClasses.length]);
    }

    /**
     * Returns all classes which are allowed for loading.
     *
     * @return allowed classes for loading by name
     */
    public String[] getAllowedClasses() {
        return allowedClasses;
    }

    @Override
    public LocalTypeWrapper<T> adaptToJson(T obj) throws Exception {
        LocalTypeWrapper<T> wrapper = new LocalTypeWrapper<>();
        wrapper.setClassName(obj.getClass().getName());
        wrapper.setInstance(obj);
        return wrapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final T adaptFromJson(LocalTypeWrapper<T> obj) throws Exception {
        if (!isAllowed(obj.getClassName())) {
            throw new ClassNotFoundException(obj.getClassName());
        }
        Constructor<T> constructor = (Constructor<T>) Class.forName(obj.getClassName()).getConstructor();
        T instance = constructor.newInstance();
        populateInstance(instance, obj);
        return instance;
    }

    protected abstract void populateInstance(T instance, LocalTypeWrapper<T> obj);

    private boolean isAllowed(String name) {
        for (String className : allowedClasses) {
            if (className.equals(name)) {
                return true;
            }
        }
        return false;
    }

}
