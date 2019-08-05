/*******************************************************************************
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * Sebastien Rius
 ******************************************************************************/
package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.Unmarshaller;

/**
 * Item implementation for {@link java.util.Map} fields.
 * According to JSON specification object can have only string keys, given that maps could only be parsed
 * from JSON objects, implementation is bound to String type.
 * We will also support Enum keys as well, which goes beyond the JSON-B 1.0 spec
 *
 * @author Roman Grigoriadi
 */
public class MapDeserializer<T extends Map<?,?>> extends AbstractContainerDeserializer<T> implements EmbeddedItem {

    private final Type mapKeyRuntimeType;
    private final Type mapValueRuntimeType;

    private final T instance;
    
    /**
     * Create instance of current item with its builder.
     *
     * @param builder {@link DeserializerBuilder} used to build this instance
     */
    protected MapDeserializer(DeserializerBuilder builder) {
        super(builder);
        mapKeyRuntimeType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[0])
                : String.class;
        mapValueRuntimeType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[1])
                : Object.class;
        this.instance = createInstance(builder);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private T createInstance(DeserializerBuilder builder) {
        Class<?> rawType = ReflectionUtils.getRawType(getRuntimeType());
        if (rawType.isInterface()) {
            return (T) getMapImpl(rawType, builder);
        } else if (EnumMap.class.isAssignableFrom(rawType)) {
            return (T) new EnumMap<>((Class<Enum>) mapKeyRuntimeType);
        } else {
            return (T) builder.getJsonbContext().getInstanceCreator().createInstance(rawType);
        }
    }

    private Map<?,?> getMapImpl(Class<?> ifcType, DeserializerBuilder builder) {
        // SortedMap, NavigableMap
        if (SortedMap.class.isAssignableFrom(ifcType)) {
            Class<?> defaultMapImplType = builder.getJsonbContext().getConfigProperties().getDefaultMapImplType();
            return SortedMap.class.isAssignableFrom(defaultMapImplType) ?
                    (Map<?,?>) builder.getJsonbContext().getInstanceCreator().createInstance(defaultMapImplType) :
                    new TreeMap<>();
        }
        return new HashMap<>();
    }

    @Override
    public T getInstance(Unmarshaller unmarshaller) {
        return instance;
    }

    @Override
    public void appendResult(Object result) {
        appendCaptor(parserContext.getLastKeyName(), convertNullToOptionalEmpty(mapValueRuntimeType, result));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <V> void appendCaptor(String key, V value) {
        if (Enum.class.isAssignableFrom((Class<?>) mapKeyRuntimeType)) {
            Enum<?> enumKey = Enum.valueOf((Class<Enum>) mapKeyRuntimeType, key);
            ((Map<Enum<?>, V>) getInstance(null)).put(enumKey, value);
        } else {
            ((Map<String, V>) getInstance(null)).put(key, value);
        }
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        final JsonbDeserializer<?> deserializer = newCollectionOrMapItem(mapValueRuntimeType, context.getJsonbContext());
        appendResult(deserializer.deserialize(parser, context, mapValueRuntimeType));
    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_OBJECT);
        return parser.getCurrentLevel();
    }
}
