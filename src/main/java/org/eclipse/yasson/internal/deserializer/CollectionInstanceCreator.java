/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.InstanceCreator;
import org.eclipse.yasson.internal.ReflectionUtils;

/**
 * Collection instance creator.
 */
class CollectionInstanceCreator implements ModelDeserializer<JsonParser> {

    private final CollectionDeserializer delegate;
    private final Type type;
    private final Class<?> clazz;
    private final boolean isEnumSet;

    CollectionInstanceCreator(CollectionDeserializer delegate, Type type) {
        this.delegate = delegate;
        this.clazz = implementationClass(ReflectionUtils.getRawType(type));
        this.isEnumSet = EnumSet.class.isAssignableFrom(clazz);
        this.type = isEnumSet ? ((ParameterizedType) type).getActualTypeArguments()[0] : type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(JsonParser value, DeserializationContextImpl context) {
        Object instance;
        if (isEnumSet) {
            instance = EnumSet.noneOf((Class<Enum>) type);
        } else {
            instance = InstanceCreator.createInstance(clazz);
        }
        context.setInstance(instance);
        return delegate.deserialize(value, context);
    }

    private Class<?> implementationClass(Class<?> type) {
        if (type.isInterface()) {
            return createInterfaceInstance(type);
        }
        return type;
    }

    private Class<?> createInterfaceInstance(Class<?> ifcType) {
        if (List.class.isAssignableFrom(ifcType)) {
            return ArrayList.class;
        }
        if (Set.class.isAssignableFrom(ifcType)) {
            if (SortedSet.class.isAssignableFrom(ifcType)) {
                return TreeSet.class;
            }
            return HashSet.class;
        }
        if (Queue.class.isAssignableFrom(ifcType)) {
            return ArrayDeque.class;
        }
        if (Collection.class == ifcType) {
            return ArrayList.class;
        }
        return ifcType;
    }
}
