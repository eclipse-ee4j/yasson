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
 * Tomas Kraus
 ******************************************************************************/
package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.RuntimeTypeInfo;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Internal container de-serializing interface.
 *
 * @param <T> container type
 */
public interface ContainerDeserializer<T> extends JsonbDeserializer<T> {

	/** Parser context. */
    public static class Context {

        /** Whether to continue with parsing on this level. */
        private boolean parse;

        /** JSON parser. */
        private final JsonParser parser;

        /** State holder for current json structure level. */
        private final JsonbRiParser.LevelContext parserContext;

        /** Current de-serialization context. */
        private final Unmarshaller unmarshallerContext;

        /**
         * Creates an instance of parser context.
         *
         * @param parser JSON parser
         * @param parserContext state holder for current json structure level
         * @param unmarshallerContext JSON-B unmarshaller
         */
        public Context(JsonParser parser, JsonbRiParser.LevelContext parserContext, Unmarshaller unmarshallerContext) {
            this.parser = parser;
            this.parserContext = parserContext;
            this.unmarshallerContext = unmarshallerContext;
            this.parse = true;
        }

        /**
         * Check whether to continue with parsing on this level.
         *
         * @return parsing shall continue when {@code true} or shall finish when {@code false}
         */
        private boolean parse() {
            return parse;
        }

        /**
         * Order parser to finish.
         *
         * Parser will finish before reading next JSON token.
         */
        public void finish() {
            this.parse = true;
        }

        /**
         * Get JSON parser.
         *
         * @return JSON parser
         */
        public JsonParser getParser() {
            return parser;
        }

        /**
         * Get state holder for current json structure level.
         *
         * @return state holder for current json structure level
         */
        public JsonbRiParser.LevelContext getJsonContext() {
            return parserContext;
        }

        /**
         * Get JSON-B unmarshaller.
         *
         * @return JSON-B unmarshaller
         */
        public Unmarshaller getUnmarshallerContext() {
            return unmarshallerContext;
        }

    }

    /**
     * Resolve {@code Map} key type.
     *
     * @param item item containing wrapper class of a type field, shall not be {@code null}
     * @param mapType type to resolve, typically field type or generic bound, shall not be {@code null}
     * @return resolved {@code Map} key type
     */
    public static Type mapKeyType(RuntimeTypeInfo item, Type mapType) {
        return mapType instanceof ParameterizedType
                ? ReflectionUtils.resolveType(item, ((ParameterizedType)mapType).getActualTypeArguments()[0])
                : Object.class;
    }

    /**
     * Resolve {@code Map} value type.
     *
     * @param item item containing wrapper class of a type field, shall not be {@code null}
     * @param mapType type to resolve, typically field type or generic bound, shall not be {@code null}
     * @return resolved {@code Map} value type
     */
    public static Type mapValueType(RuntimeTypeInfo item, Type mapType) {
        return mapType instanceof ParameterizedType
                ? ReflectionUtils.resolveType(item, ((ParameterizedType)mapType).getActualTypeArguments()[1])
                : Object.class;
    }

    /**
     * Creates an instance of {@code Map} being de-serialized.
     *
     * @param <T> type of {@code Map} instance to be returned
     * @param builder de-serializer builder
     * @param mapType type of returned {@code Map} instance
     * @return created {@code Map} instance
     */
    @SuppressWarnings("unchecked")
    public static <T extends Map<?,?>> T createMapInstance(DeserializerBuilder builder, Type mapType) {
        Class<?> rawType = ReflectionUtils.getRawType(mapType);
        if (rawType.isInterface()) {
            if (SortedMap.class.isAssignableFrom(rawType)) {
                Class<?> defaultMapImplType = builder.getJsonbContext().getConfigProperties().getDefaultMapImplType();
                return SortedMap.class.isAssignableFrom(defaultMapImplType)
                        ? (T) builder.getJsonbContext().getInstanceCreator().createInstance(defaultMapImplType)
                        : (T) new TreeMap<>();
            } else {
                return (T) new HashMap<>();
            }
        } else {
            return (T) builder.getJsonbContext().getInstanceCreator().createInstance(rawType);
        }
    }

    /**
     * Builds new de-serializer for {@code Collection} or {@code Map} item (key or value).
     *
     * @param wrapper item wrapper. {@code Collection} or {@code Map} instance.
     * @param valueType type of deserialized value
     * @param ctx JSON-B parser context
     * @param event JSON parser event
     * @return de-serializer for {@code Collection} or {@code Map} item
     */
    public static JsonbDeserializer<?> newCollectionOrMapItem(CurrentItem<?> wrapper, Type valueType, JsonbContext ctx, JsonParser.Event event) {
        //TODO needs performance optimization on not to create deserializer each time
        //TODO In contrast to serialization value type cannot change here
        Type actualValueType = ReflectionUtils.resolveType(wrapper, valueType);
        DeserializerBuilder deserializerBuilder = newUnmarshallerItemBuilder(wrapper, ctx, event).withType(actualValueType);
        if (!DefaultSerializers.getInstance().isKnownType(ReflectionUtils.getRawType(actualValueType))) {
            ClassModel classModel = ctx.getMappingContext().getOrCreateClassModel(ReflectionUtils.getRawType(actualValueType));
            deserializerBuilder.withCustomization(classModel == null ? null : classModel.getCustomization());
        }
        return deserializerBuilder.build();
    }

    /**
     * Creates new instance of {@code DeserializerBuilder}.
     *
     * @param wrapper item wrapper. {@code Collection} or {@code Map} instance.
     * @param ctx JSON-P parser context
     * @param event JSON parser event
     * @return new instance of {@code DeserializerBuilder}
     */
    public static DeserializerBuilder newUnmarshallerItemBuilder(CurrentItem<?> wrapper, JsonbContext ctx, JsonParser.Event event) {
        return new DeserializerBuilder(ctx).withWrapper(wrapper).withJsonValueType(event);
    }

    /**
     * De-serialize container stored as JSON structure.
     * Reads JSON tokens from JSON parser and calls corresponding handler method for each of the tokens.
     * Implementing class shall process those tokens and build container instance of {@code T} to be returned.
     *
     * @param parser JSON parser
     * @param context de-serialization context
     * @param rtType type of returned instance
     * @return {@code Map} instance with content of source JSON structure
     */
    @Override
    default T deserialize(final JsonParser parser, DeserializationContext context, Type rtType) {
        final Context ctx = new Context(parser, moveToFirst((JsonbParser)parser), (Unmarshaller) context);
        while (parser.hasNext() && ctx.parse()) {
            final JsonParser.Event event = parser.next();
            switch (event) {
                case START_ARRAY:  startArray(ctx, event);  break;
                case START_OBJECT: startObject(ctx, event); break;
                case KEY_NAME:     keyName(ctx, event);     break;
                case VALUE_STRING: valueString(ctx, event); break;
                case VALUE_NUMBER: valueNumber(ctx, event); break;
                case VALUE_TRUE:   valueTrue(ctx, event);   break;
                case VALUE_FALSE:  valueFalse(ctx, event);  break;
                case VALUE_NULL:   valueNull(ctx, event);   break;
                case END_ARRAY:    endArray(ctx, event);    break;
                case END_OBJECT:   endObject(ctx, event);   break;
                default:
                    throw new JsonbException(Messages.getMessage(MessageKeys.NOT_VALUE_TYPE, event));
            }
        }
        return getInstance();
    }

    /**
     * Return container instance.
     *
     * @return container instance built by de-serializer
     */
    T getInstance();

    /**
     * Move to the first event for current deserializer structure.
     *
     * @param parser JSON parser
     * @return first event
     */
    JsonbRiParser.LevelContext moveToFirst(JsonbParser parser);

    /**
     * Received {@code '['} (JSON array opening) symbol from JSON parser.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void startArray(Context ctx, JsonParser.Event event);

    /**
     * Received <pre>'{'</pre> (JSON object opening) symbol from JSON parser.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void startObject(Context ctx, JsonParser.Event event);

    /**
     * Received {@code name} of JSON object attribute.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void keyName(Context ctx, JsonParser.Event event);

    /**
     * Received JSON String value.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void valueString(Context ctx, JsonParser.Event event);

    /**
     * Received JSON Number value.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void valueNumber(Context ctx, JsonParser.Event event);

    /**
     * Received JSON boolean value {@code true}.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void valueTrue(Context ctx, JsonParser.Event event);

    /**
     * Received JSON boolean value {@code false}.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void valueFalse(Context ctx, JsonParser.Event event);

    /**
     * Received JSON value {@code null}.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void valueNull(Context ctx, JsonParser.Event event);

    /**
     * Received {@code ']'} (JSON array closing) symbol from JSON parser.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void endArray(Context ctx, JsonParser.Event event);

    /**
     * Received <pre>'{'</pre> (JSON object closing) symbol from JSON parser.
     *
     * @param ctx parser context
     * @param event JSON parser event
     */
    void endObject(Context ctx, JsonParser.Event event);

}
