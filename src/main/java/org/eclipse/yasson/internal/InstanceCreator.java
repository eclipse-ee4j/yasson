/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
    private final Map<Class<?>, Supplier<?>> creators;

    public InstanceCreator() {
        creators = new HashMap<>();
        creators.put(ArrayList.class, ArrayList::new);
        creators.put(LinkedList.class, LinkedList::new);
        creators.put(HashSet.class, HashSet::new);
        creators.put(TreeSet.class, TreeSet::new);
        creators.put(HashMap.class, HashMap::new);
        creators.put(TreeMap.class, TreeMap::new);
    }

    /**
     * Create an instance of the given class with its default constructor.
     * @param tClass class to create instance
     * @param <T> Type of the class/instance
     * @return crated instance
     */
    @SuppressWarnings("unchecked")
    public <T> T createInstance(Class<T> tClass) {
        Supplier<?> creator = creators.get(tClass);
        //No worries for race conditions here, instance may be replaced during first attempt.
        if (creator == null) {
            creator = createConstructorSupplier(ReflectionUtils.getDefaultConstructor(tClass, true));
            creators.put(tClass, creator);
        }

        return (T) creator.get();
    }
    
    private static Supplier<?> createConstructorSupplier(Constructor<?> constructor){
    	return () -> ReflectionUtils.createNoArgConstructorInstance(constructor);
    }
}
