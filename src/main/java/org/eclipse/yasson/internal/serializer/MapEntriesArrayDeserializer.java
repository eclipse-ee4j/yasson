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

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.Type;
import java.util.Map;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * De-serialize JSON array of map entries JSON objects as {@link Map}.
 * JSON array of map entries JSON objects:
 * <pre>
 * [
 *     {
 *         "key": JsonValue,
 *         "value": JsonValue
 *     }, ...
 * ]
 * </pre>
 *
 * @param <K> {@link Map} key type to serialize
 * @param <V> {@link Map} value type to serialize
 */
public class MapEntriesArrayDeserializer<K, V> extends AbstractItem<Map<K, V>> implements JsonbDeserializer<Map<K, V>> {

    /**
     * Map entries parser internal state.
     */
    private enum State {
        /**
         * Expecting the beginning of next Map entry JSON object.
         */
        NEXT_ENTRY,
        /**
         * Expecting Map entry key ("key" or "value").
         */
        ENTRY_KEY,
        /**
         * Expecting Map entry value related to key.
         */
        ENTRY_KEY_OBJECT,
        /**
         * Expecting Map entry value related to value.
         */
        ENTRY_VALUE_OBJECT,
        /**
         * Expecting the end of current Map entry JSON object.
         */
        ARRAY_END
    }

    // Finite-state machine transition table:
    // --------------------------------------
    // NEXT_ENTRY:
    //  * START_OBJECT -> ENTRY_KEY
    //  * END_ARRAY    -> ARRAY_END (terminal state, exit parser)
    // ENTRY_KEY:
    //  * KEY_NAME('key)   -> ENTRY_KEY_OBJECT
    //  * KEY_NAME('value) -> ENTRY_VALUE_OBJECT
    //  * END_OBJECT       -> NEXT_ENTRY
    // ENTRY_KEY_OBJECT:
    //  * START_OBJECT -> external parser -> ENTRY_KEY
    //  * START_ARRAY  -> external parser -> ENTRY_KEY
    //  * VALUE_STRING -> external parser -> ENTRY_KEY
    //  * VALUE_NUMBER -> external parser -> ENTRY_KEY
    //  * VALUE_TRUE   -> external parser -> ENTRY_KEY
    //  * VALUE_FALSE  -> external parser -> ENTRY_KEY
    //  * VALUE_NULL   -> external parser -> ENTRY_KEY
    // ENTRY_VALUE_OBJECT:
    //  * START_OBJECT -> external parser -> ENTRY_KEY
    //  * START_ARRAY  -> external parser -> ENTRY_KEY
    //  * VALUE_STRING -> external parser -> ENTRY_KEY
    //  * VALUE_NUMBER -> external parser -> ENTRY_KEY
    //  * VALUE_TRUE   -> external parser -> ENTRY_KEY
    //  * VALUE_FALSE  -> external parser -> ENTRY_KEY
    //  * VALUE_NULL   -> external parser -> ENTRY_KEY
    // ARRAY_END: No additional JSON token processing is allowed in this state.
    //            JSON array of map entries parser must finish immediately.
    //
    // External parser shall process whole JSON value following 'key' or 'value' MapEntry JSON Object
    // attribute identifiers. Finite-state machine just moves to ENTRY_KEY state to process next MapEntry
    // attribute or to finish current MapEntry parsing when END_OBJECT token was received.

    // JSON tokens are mapped to event method calls defined in ContainerDeserializer:
    //  * START_ARRAY  -> startArray
    //  * START_OBJECT -> startObject
    //  * KEY_NAME     -> keyName
    //  * VALUE_STRING -> simpleValue
    //  * VALUE_NUMBER -> simpleValue
    //  * VALUE_TRUE   -> simpleValue
    //  * VALUE_FALSE  -> simpleValue
    //  * VALUE_NULL   -> valueNull
    //  * END_ARRAY    -> endArray
    //  * END_OBJECT   -> endObject
    // so JSON token dispatching is already implemented in ContainerDeserializer interface.
    // Each method needs just current state dispatcher (switch statement) to implement finite-state machine
    // transition function T(token, state) -> state

    /**
     * Internal container de-serializer context.
     */
    static class Context {

        /**
         * Whether to continue with parsing on this level.
         */
        private boolean parse;

        /**
         * JSON parser.
         */
        private final JsonParser parser;

        /**
         * Current de-serialization context.
         */
        private final Unmarshaller unmarshallerContext;

        /**
         * Creates an instance of parser context.
         *
         * @param parser              JSON parser
         * @param parserContext       state holder for current json structure level
         * @param unmarshallerContext JSON-B unmarshaller
         */
        Context(JsonParser parser, Unmarshaller unmarshallerContext) {
            this.parser = parser;
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
            this.parse = false;
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
         * Get JSON-B unmarshaller.
         *
         * @return JSON-B unmarshaller
         */
        public Unmarshaller getUnmarshallerContext() {
            return unmarshallerContext;
        }

    }

    /**
     * Default property name for map entry key.
     */
    private static final String DEFAULT_KEY_ENTRY_NAME = "key";

    /**
     * Default property name for map entry value.
     */
    private static final String DEFAULT_VALUE_ENTRY_NAME = "value";

    /**
     * Instance of Map to be returned.
     */
    private final Map<K, V> instance;

    /**
     * Type of map key.
     */
    private final Type mapKeyType;

    /**
     * Type of map value.
     */
    private final Type mapValueType;

    /**
     * Map entries parser internal state.
     */
    private State state;

    /**
     * Map entry key.
     */
    private K key;

    /**
     * Map entry value.
     */
    private V value;

    /**
     * Property name for map entry key.
     */
    private final String keyEntryName;

    /**
     * Property name for map entry value.
     */
    private final String valueEntryName;

    /**
     * Creates an instance of {@code Map} entries array de-serializer.
     *
     * @param builder de-serializer builder
     */
    MapEntriesArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
        final Type mapType = getRuntimeType();
        this.mapKeyType = ContainerDeserializerUtils.mapKeyType(this, mapType);
        this.mapValueType = ContainerDeserializerUtils.mapValueType(this, mapType);
        this.instance = ContainerDeserializerUtils.createMapInstance(builder, mapType);
        this.state = State.NEXT_ENTRY;
        this.keyEntryName = DEFAULT_KEY_ENTRY_NAME;
        this.valueEntryName = DEFAULT_VALUE_ENTRY_NAME;
    }

    /**
     * De-serialize container stored as JSON structure.
     * Reads JSON tokens from JSON parser and calls corresponding handler method for each of the tokens.
     * Implementing class shall process those tokens and build container instance of {@code T} to be returned.
     *
     * @param parser  JSON parser
     * @param context de-serialization context
     * @param rtType  type of returned instance
     * @return {@code Map} instance with content of source JSON structure
     */
    @Override
    public Map<K, V> deserialize(final JsonParser parser, DeserializationContext context, Type rtType) {
        final Context ctx = new Context(parser, (Unmarshaller) context);
        ((JsonbParser) ctx.parser).moveTo(JsonParser.Event.START_ARRAY);
        while (parser.hasNext() && ctx.parse()) {
            final JsonParser.Event event = parser.next();
            switch (event) {
            case START_ARRAY:
                startArray(ctx, event);
                break;
            case START_OBJECT:
                startObject(ctx, event);
                break;
            case KEY_NAME:
                keyName(ctx, event);
                break;
            case VALUE_STRING:
            case VALUE_NUMBER:
            case VALUE_TRUE:
            case VALUE_FALSE:
                simpleValue(ctx, event);
                break;
            case VALUE_NULL:
                valueNull(ctx, event);
                break;
            case END_ARRAY:
                endArray(ctx, event);
                break;
            case END_OBJECT:
                endObject(ctx, event);
                break;
            default:
                throw new JsonbException(Messages.getMessage(MessageKeys.NOT_VALUE_TYPE, event));
            }
        }
        return instance;
    }

    /**
     * De-serialize JSON structure following beginning of JSON Array ('[').
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    public void startArray(Context ctx, JsonParser.Event event) {
        switch (state) {
        case ENTRY_KEY_OBJECT:
            key = deserializeContent(ctx, mapKeyType, event);
            break;
        case ENTRY_VALUE_OBJECT:
            value = deserializeContent(ctx, mapValueType, event);
            break;
        default:
            handleSyntaxError(state, event);
        }
        state = State.ENTRY_KEY;
    }

    /**
     * De-serialize JSON structure following beginning of JSON Object ('{').
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    private void startObject(Context ctx, JsonParser.Event event) {
        switch (state) {
        case NEXT_ENTRY:
            clearMapEntry();
            break;
        case ENTRY_KEY_OBJECT:
            key = deserializeContent(ctx, mapKeyType, event);
            break;
        case ENTRY_VALUE_OBJECT:
            value = deserializeContent(ctx, mapValueType, event);
            break;
        default:
            handleSyntaxError(state, event);
        }
        state = State.ENTRY_KEY;
    }

    /**
     * De-serialize Map.Entry key values ("key" or "value") and select proper state transition to deserialize
     * following key or value data.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    private void keyName(Context ctx, JsonParser.Event event) {
        if (state == State.ENTRY_KEY) {
            final String key = ctx.getParser().getString();
            if (keyEntryName.equals(key)) {
                state = State.ENTRY_KEY_OBJECT;
            } else if (valueEntryName.equals(key)) {
                state = State.ENTRY_VALUE_OBJECT;
            } else {
                throw new JsonbException("Invalid Map entry key: " + key);
            }
        } else {
            handleSyntaxError(state, event);
        }
    }

    /**
     * De-serialize simple JSON value (primitive types, String).
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    private void simpleValue(Context ctx, JsonParser.Event event) {
        switch (state) {
        case ENTRY_KEY_OBJECT:
            key = deserializeContent(ctx, mapKeyType, event);
            break;
        case ENTRY_VALUE_OBJECT:
            value = deserializeContent(ctx, mapValueType, event);
            break;
        default:
            handleSyntaxError(state, event);
        }
        state = State.ENTRY_KEY;
    }

    /**
     * De-serialize JSON value {@code null}.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    private void valueNull(Context ctx, JsonParser.Event event) {
        switch (state) {
        case ENTRY_KEY_OBJECT:
        case ENTRY_VALUE_OBJECT:
            break;
        default:
            handleSyntaxError(state, event);
        }
        state = State.ENTRY_KEY;
    }

    /**
     * De-serialize end of JSON Array when '[' character is received.
     * This is the last step of Map processing. Reading of JSON tokens from parser on this level shall finish.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    private void endArray(Context ctx, JsonParser.Event event) {
        if (state == State.NEXT_ENTRY) {
            ctx.finish();
        } else {
            handleSyntaxError(state, event);
        }
        state = State.ARRAY_END;
    }

    /**
     * De-serialize end of Map.Entry JSON Object when '{' character is received.
     * This is the last step of current Map.Entry processing.
     * Key and value data were already processed so they are stored into the Map now.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    private void endObject(Context ctx, JsonParser.Event event) {
        if (state == State.ENTRY_KEY) {
            instance.put(key, value);
        } else {
            handleSyntaxError(state, event);
        }
        state = State.NEXT_ENTRY;
    }

    // It's switch called from switch, but it simplified proper error message selection depending
    // on current state and token.

    /**
     * Throw more specific exception for map deserialization JSON parser syntax errors.
     *
     * @param state current state
     * @param event current JSON token
     */
    private static void handleSyntaxError(State state, JsonParser.Event event) {
        switch (state) {
        // Error handling for individual states and undefined transition from them.
        case NEXT_ENTRY:
            throw new JsonbException("Map deserialization error: got " + event.name()
                                             + " when expecting beginning of map entry JSON object or end of whole map entries "
                                             + "array");
        case ENTRY_KEY:
            throw new JsonbException("Map deserialization error: got " + event.name()
                                             + " when expecting map entry attribute name 'key' or 'value' or end of map entry "
                                             + "JSON object");
        case ENTRY_KEY_OBJECT:
            throw new JsonbException("Map deserialization error: got " + event.name()
                                             + " when expecting map entry attribute value related to target map entry key");
        case ENTRY_VALUE_OBJECT:
            throw new JsonbException("Map deserialization error: got " + event.name()
                                             + " when expecting map entry attribute value related to target map entry value");
            // Following cases are theoretically unreachable, but let's have full states list to handle coding error
        case ARRAY_END:
            throw new JsonbException("Map deserialization error: got " + event.name()
                                             + " when current map deserialization was already finished");
        default:
            throw new IllegalStateException("Unknown map deserialization parser state: " + state.name());
        }
    }

    /**
     * Deserialize key or value content using proper de-serializer.
     *
     * @param ctx         parser context
     * @param contentType type of content to be de-serialized
     * @param event       JSON parser token (event)
     * @return de-serialized key or value content to be stored into {@code Map}
     */
    @SuppressWarnings("unchecked")
    private <T> T deserializeContent(Context ctx, Type contentType, JsonParser.Event event) {
        final JsonbDeserializer<?> deserializer = ContainerDeserializerUtils
                .newCollectionOrMapItem(this, contentType, ctx.getUnmarshallerContext().getJsonbContext(), event);
        return (T) deserializer.deserialize(ctx.getParser(), ctx.getUnmarshallerContext(), contentType);
    }

    /**
     * Clear internal Map.Entry storage before processing next entry.
     */
    private void clearMapEntry() {
        key = null;
        value = null;
    }

}
