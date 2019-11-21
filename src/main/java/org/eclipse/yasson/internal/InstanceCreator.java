/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * Creates instances for known types, caches constructors of unknown.
 * (Constructors of parsed types are stored in {@link org.eclipse.yasson.internal.model.ClassModel}).
 */
public class InstanceCreator {

    private static final InstanceCreator INSTANCE = new InstanceCreator();

    static InstanceCreator getSingleton() {
        return INSTANCE;
    }

    private static final Map<Class, Supplier> CREATORS = new HashMap<>();

    static {
        CREATORS.put(ArrayList.class, ArrayList::new);
        CREATORS.put(LinkedList.class, LinkedList::new);
        CREATORS.put(HashSet.class, HashSet::new);
        CREATORS.put(TreeSet.class, TreeSet::new);
        CREATORS.put(HashMap.class, HashMap::new);
        CREATORS.put(TreeMap.class, TreeMap::new);
    }

    private InstanceCreator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("This class should never be instantiated");
        }
    }

    /**
     * Create an instance of the given class with its default constructor.
     *
     * @param tClass class to create instance
     * @param <T>    Type of the class/instance
     * @return crated instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<T> tClass) {
        Supplier<T> creator = CREATORS.get(tClass);
        //No worries for race conditions here, instance may be replaced during first attempt.
        if (creator == null) {
            Constructor<T> constructor = ReflectionUtils.getDefaultConstructor(tClass, true);
            creator = () -> ReflectionUtils.createNoArgConstructorInstance(constructor);
            CREATORS.put(tClass, creator);
        }

        return creator.get();
    }

}
