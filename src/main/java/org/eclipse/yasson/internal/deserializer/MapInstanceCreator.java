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

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.InstanceCreator;
import org.eclipse.yasson.internal.JsonbConfigProperties;

/**
 * Map instance creator.
 */
class MapInstanceCreator implements ModelDeserializer<JsonParser> {

    private final MapDeserializer delegate;
    private final JsonbConfigProperties configProperties;
    private final Class<?> clazz;

    MapInstanceCreator(MapDeserializer delegate,
                       JsonbConfigProperties configProperties,
                       Class<?> clazz) {
        this.delegate = delegate;
        this.configProperties = configProperties;
        this.clazz = clazz;
    }

    @Override
    public Object deserialize(JsonParser value, DeserializationContextImpl context) {
        Map<?, ?> map = createInstance(clazz);
        context.setInstance(map);
        return delegate.deserialize(value, context);
    }

    private Map<?, ?> createInstance(Class<?> clazz) {
        return clazz.isInterface()
                ? getMapImpl(clazz)
                : (Map<?, ?>) InstanceCreator.createInstance(clazz);
    }

    private Map<?, ?> getMapImpl(Class<?> ifcType) {
        if (ConcurrentMap.class.isAssignableFrom(ifcType)) {
            if (SortedMap.class.isAssignableFrom(ifcType) || NavigableMap.class.isAssignableFrom(ifcType)) {
                return new ConcurrentSkipListMap<>();
            } else {
                return new ConcurrentHashMap<>();
            }
        }
        // SortedMap, NavigableMap
        if (SortedMap.class.isAssignableFrom(ifcType)) {
            Class<?> defaultMapImplType = configProperties.getDefaultMapImplType();
            return SortedMap.class.isAssignableFrom(defaultMapImplType)
                    ? (Map<?, ?>) InstanceCreator.createInstance(defaultMapImplType)
                    : new TreeMap<>();
        }
        return new HashMap<>();
    }

}
