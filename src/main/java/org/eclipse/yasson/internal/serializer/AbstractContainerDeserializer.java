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

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Base class for all deserializers producing non single value result.
 * Deserialize bean objects, collections, maps, arrays, etc.
 *
 * @param <T> container type
 */
public abstract class AbstractContainerDeserializer<T> extends AbstractItem<T> implements JsonbDeserializer<T> {

    private JsonbRiParser.LevelContext parserContext;

    /**
     * Create instance of current item with its builder.
     *
     * @param builder {@link DeserializerBuilder} used to build this instance
     */
    AbstractContainerDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    /**
     * Drives JSONP {@link JsonParser} to deserialize json document.
     *
     * @param parser  JSON parser.
     * @param context Deseriaization context.
     * @param rtType  Runtime type.
     * @return Instance of a type for this item.
     */
    @Override
    public final T deserialize(JsonParser parser, DeserializationContext context, Type rtType) {
        Unmarshaller ctx = (Unmarshaller) context;
        deserializeInternal((JsonbParser) parser, ctx);
        return getInstance((Unmarshaller) context);
    }

    /**
     * Creates and initializes an instance of deserializing item.
     *
     * @param unmarshaller Current deserialization context.
     * @return An instance of deserializing item.
     */
    protected abstract T getInstance(Unmarshaller unmarshaller);

    /**
     * Deserialize specific item type.
     *
     * @param parser  jsonb parser
     * @param context context
     */
    protected void deserializeInternal(JsonbParser parser, Unmarshaller context) {
        parserContext = moveToFirst(parser);
        while (parser.hasNext()) {
            final JsonParser.Event event = parser.next();
            switch (event) {
            case START_OBJECT:
            case START_ARRAY:
            case VALUE_STRING:
            case VALUE_NUMBER:
            case VALUE_FALSE:
            case VALUE_TRUE:
                try {
                    deserializeNext(parser, context);
                } catch (JsonbException e) {
                    if (parserContext == null || parserContext.getLastKeyName() == null) {
                        throw e;
                    } else {
                        throw new JsonbException("Unable to deserialize property '" + parserContext.getLastKeyName()
                                                         + "' because of: " + e.getMessage(), e);
                    }
                }
                break;
            case KEY_NAME:
                break;
            case VALUE_NULL:
                appendResult(null);
                break;
            case END_OBJECT:
            case END_ARRAY:
                return;
            default:
                throw new JsonbException(Messages.getMessage(MessageKeys.NOT_VALUE_TYPE, event));
            }
        }
    }

    /**
     * Determine class mappings and create an instance of a new deserializer.
     * Currently processed deserializer is pushed to stack, for waiting till new object is finished.
     *
     * @param parser  Json parser.
     * @param context Current unmarshalling context.
     */
    protected abstract void deserializeNext(JsonParser parser, Unmarshaller context);

    /**
     * Move to first event for current deserializer structure.
     *
     * @param parser Json parser.
     * @return First event.
     */
    protected abstract JsonbRiParser.LevelContext moveToFirst(JsonbParser parser);

    /**
     * Returns new deserialization builder for specific item.
     *
     * @param ctx jsonb context
     * @return deserialization builder
     */
    protected DeserializerBuilder newUnmarshallerItemBuilder(JsonbContext ctx) {
        return ContainerDeserializerUtils.newUnmarshallerItemBuilder(this, ctx, parserContext.getLastEvent());
    }

    /**
     * Returns new deserialization builder for specific collection or map.
     *
     * @param valueType value type
     * @param ctx       jsonb context
     * @return deserialization builder
     */
    protected JsonbDeserializer<?> newCollectionOrMapItem(Type valueType, JsonbContext ctx) {
        return ContainerDeserializerUtils.newCollectionOrMapItem(this, valueType, ctx, parserContext.getLastEvent());
    }

    /**
     * If value is null and property model type is one of {@link Optional}, {@link OptionalDouble},
     * {@link OptionalInt}, or {@link OptionalLong}, value of corresponding {@code Optional#empty()}
     * is returned.
     *
     * @param propertyType property type
     * @param value        value to set
     * @return empty optional if applies
     */
    protected Object convertNullToOptionalEmpty(Type propertyType, Object value) {
        if (value != null) {
            return value;
        }

        if (!(propertyType instanceof Class)) {
            propertyType = ReflectionUtils.getRawType(ReflectionUtils.resolveType(this, propertyType));
        }

        if (propertyType == Optional.class) {
            return Optional.empty();
        } else if (propertyType == OptionalInt.class) {
            return OptionalInt.empty();
        } else if (propertyType == OptionalLong.class) {
            return OptionalLong.empty();
        } else if (propertyType == OptionalDouble.class) {
            return OptionalDouble.empty();
        } else {
            return null;
        }
    }

    /**
     * After object is transitively deserialized from JSON, "append" it to its wrapper.
     * In case of a field set value to field, in case of collections
     * or other embedded objects use methods provided.
     *
     * @param result An instance result of an item.
     */
    public abstract void appendResult(Object result);

    /**
     * Returns parser context.
     *
     * @return parser context
     */
    JsonbRiParser.LevelContext getParserContext() {
        return parserContext;
    }

    /**
     * Sets new parser context.
     *
     * @param parserContext parser context
     */
    void setParserContext(JsonbRiParser.LevelContext parserContext) {
        this.parserContext = parserContext;
    }
}
