/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.Unmarshaller;

import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Item implementation for {@link java.util.List} fields
 *
 * @author Roman Grigoriadi
 */
class CollectionDeserializer<T extends Collection<?>> extends AbstractContainerDeserializer<T> implements EmbeddedItem {

    /**
     * Generic bound parameter of List.
     */
    private final Type collectionValueType;

    private T instance;

    /**
     * @param builder {@link DeserializerBuilder) used to build this instance
     */
    protected CollectionDeserializer(DeserializerBuilder builder) {
        super(builder);
        collectionValueType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[0])
                : Object.class;

        instance = createInstance(builder);
    }

    @SuppressWarnings("unchecked")
    private T createInstance(DeserializerBuilder builder) {
        Class<T> rawType = (Class<T>) ReflectionUtils.getRawType(getRuntimeType());

        if (rawType.isInterface()) {
            final T x = createInterfaceInstance(rawType);
            if (x != null) return x;
        }
        return builder.getJsonbContext().getInstanceCreator().createInstance(rawType);
    }

    @SuppressWarnings("unchecked")
    private T createInterfaceInstance(Class<?> ifcType) {
        if (List.class.isAssignableFrom(ifcType)) {
            if (LinkedList.class == ifcType) {
                return (T) new LinkedList();
            }
            return (T) new ArrayList<>();
        }
        if (Set.class.isAssignableFrom(ifcType)) {
            if (SortedSet.class.isAssignableFrom(ifcType)) {
                return (T) new TreeSet<>();
            }
            return (T) new HashSet<>();
        }
        if (Queue.class.isAssignableFrom(ifcType)) {
            return (T) new ArrayDeque<>();
        }
        if (Collection.class == ifcType) {
            return (T) new ArrayList();
        }
        return null;
    }

    @Override
    public T getInstance(Unmarshaller unmarshaller) {
        return instance;
    }

    @Override
    public void appendResult(Object result) {
        appendCaptor(convertNullToOptionalEmpty(collectionValueType, result));
    }

    @SuppressWarnings("unchecked")
    private <T> void appendCaptor(T object) {
        ((Collection<T>) instance).add(object);
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        final JsonbDeserializer<?> deserializer = newCollectionOrMapItem(collectionValueType, context.getJsonbContext());
        appendResult(deserializer.deserialize(parser, context, collectionValueType));
    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_ARRAY);
        return parser.getCurrentLevel();
    }
}
