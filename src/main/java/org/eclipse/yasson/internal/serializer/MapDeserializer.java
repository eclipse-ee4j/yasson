/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.Unmarshaller;

/**
 * Item implementation for {@link java.util.Map} fields.
 * According to JSON specification object can have only string keys.
 * Nevertheless the implementation lets the key be a basic object that was
 * serialized into a string representation. Therefore the key is also parsed to
 * convert it into its parametrized type.
 *
 * @param <T> map type
 */
public class MapDeserializer<T extends Map<?, ?>> extends AbstractContainerDeserializer<T> implements EmbeddedItem {

    /**
     * Type of the key in the map.
     */
    private final Type mapKeyRuntimeType;

    /**
     * Type of value in the map.
     */
    private final Type mapValueRuntimeType;

    private final T instance;

    /**
     * Create instance of current item with its builder.
     *
     * @param builder {@link DeserializerBuilder} used to build this instance
     */
    protected MapDeserializer(DeserializerBuilder builder) {
        super(builder);
        mapKeyRuntimeType = getRuntimeType() instanceof ParameterizedType
                ? ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[0])
                : Object.class;
        mapValueRuntimeType = getRuntimeType() instanceof ParameterizedType
                ? ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[1])
                : Object.class;

        this.instance = createInstance(builder);
    }

    @SuppressWarnings("unchecked")
    private T createInstance(DeserializerBuilder builder) {
        Class<?> rawType = ReflectionUtils.getRawType(getRuntimeType());
        return rawType.isInterface()
                ? (T) getMapImpl(rawType, builder)
                : (T) builder.getJsonbContext().getInstanceCreator().createInstance(rawType);
    }

    private Map getMapImpl(Class ifcType, DeserializerBuilder builder) {
        if (ConcurrentMap.class.isAssignableFrom(ifcType)) {
            if (SortedMap.class.isAssignableFrom(ifcType) || NavigableMap.class.isAssignableFrom(ifcType)) {
                return new ConcurrentSkipListMap<>();
            } else {
                return new ConcurrentHashMap<>();
            }
        }
        // SortedMap, NavigableMap
        if (SortedMap.class.isAssignableFrom(ifcType)) {
            Class<?> defaultMapImplType = builder.getJsonbContext().getConfigProperties().getDefaultMapImplType();
            return SortedMap.class.isAssignableFrom(defaultMapImplType)
                    ? (Map) builder.getJsonbContext().getInstanceCreator().createInstance(defaultMapImplType)
                    : new TreeMap<>();
        }
        return new HashMap<>();
    }

    @Override
    public T getInstance(Unmarshaller unmarshaller) {
        return instance;
    }

    @Override
    public void appendResult(Object result, Unmarshaller context) {
        // try to deserialize the string key into its type, JaxbException if not possible
        final Object key = context.deserialize(mapKeyRuntimeType, new JsonbRiParser(
                context.getJsonbContext().getJsonProvider().createParser(
                        new StringReader("\"" + getParserContext().getLastKeyName() + "\""))));
        appendCaptor(key, convertNullToOptionalEmpty(mapValueRuntimeType, result));
    }

    @SuppressWarnings("unchecked")
    private <K, V> void appendCaptor(K key, V value) {
        ((Map<K, V>) getInstance(null)).put(key, value);
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        final JsonbDeserializer<?> deserializer = newCollectionOrMapItem(mapValueRuntimeType, context.getJsonbContext());
        appendResult(deserializer.deserialize(parser, context, mapValueRuntimeType), context);
    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_OBJECT);
        return parser.getCurrentLevel();
    }
}
