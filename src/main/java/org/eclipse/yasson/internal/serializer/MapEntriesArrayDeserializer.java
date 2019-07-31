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

import java.lang.reflect.Type;
import java.util.Map;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;

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
 * @param <K> {@link Map} key type to serialize
 * @param <V> {@link Map} value type to serialize
 */
public class MapEntriesArrayDeserializer<K,V> extends AbstractItem<Map<K,V>> implements ContainerDeserializer<Map<K,V>> {

    /** Map entries parser internal state. */
    private static enum State {
        /** Expecting the beginning of next Map entry JSON object. */
        NEXT_ENTRY,
        /** Expecting Map entry key ("key" or "value"). */
        ENTRY_KEY,
        /** Expecting Map entry value related to key. */
        ENTRY_KEY_OBJECT,
        /** Expecting Map entry value related to value. */
        ENTRY_VALUE_OBJECT,
        /** Expecting the end of current Map entry JSON object. */
        ARRAY_END
    }

    /** Default property name for map entry key. */
    private static final String DEFAULT_KEY_ENTRY_NAME = "key";

    /** Default property name for map entry value. */
    private static final String DEFAULT_VALUE_ENTRY_NAME = "value";

    /** Instance of Map to be returned. */
    private final Map<K,V> instance;

    /** Type of map key. */
    private final Type mapKeyType;

    /** Type of map value. */
    private final Type mapValueType;

    /** Map entries parser internal state. */
    private State state;

    /** Map entry key. */
    private K key;

    /** Map entry value. */
    private V value;

    /** Property name for map entry key. */
    private final String keyEntryName;

    /** Property name for map entry value. */
    private final String valueEntryName;

    /**
     * Creates an instance of {@code Map} entries array de-serializer.
     *
     * @param builder de-serializer builder
     */
    MapEntriesArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
        final Type mapType = getRuntimeType();
        this.mapKeyType = ContainerDeserializer.mapKeyType(this, mapType);
        this.mapValueType = ContainerDeserializer.mapValueType(this, mapType);
        this.instance = ContainerDeserializer.createMapInstance(builder, mapType);
        this.state = State.NEXT_ENTRY;
        this.keyEntryName = DEFAULT_KEY_ENTRY_NAME;
        this.valueEntryName = DEFAULT_VALUE_ENTRY_NAME;
    }

    /**
     * De-serialize JSON structure following beginning of JSON Array ('['). 
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    @Override
    public void startArray(Context ctx, JsonParser.Event event) {
        switch (state) {
            case ENTRY_KEY_OBJECT:
                key = deserializeContent(ctx, mapKeyType, event);
                break;
            case ENTRY_VALUE_OBJECT:
                value = deserializeContent(ctx, mapValueType, event);
                break;
            default:
                throw new JsonbException("Invalid JSON entry: " + event.name());
        }
        state = State.ENTRY_KEY;
    }

    /**
     * De-serialize JSON structure following beginning of JSON Object ('{'). 
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    @Override
    public void startObject(Context ctx, JsonParser.Event event) {
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
                throw new JsonbException("Invalid JSON entry: " + event.name());
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
	@Override
	public void keyName(Context ctx, JsonParser.Event event) {
		switch (state) {
			case ENTRY_KEY:
				final String key = ctx.getParser().getString();
				if (keyEntryName.equals(key)) {
					state = State.ENTRY_KEY_OBJECT;
				} else if (valueEntryName.equals(key)) {
					state = State.ENTRY_VALUE_OBJECT;
				} else {
					throw new JsonbException("Invalid Map entry key: " + key);
				}
			    break;
			default:
				throw new JsonbException("Invalid JSON entry: " + event.name());
		}
	}

    /**
     * De-serialize JSON String value.
     * Delegates processing to common {@code void simpleValue(Context, JsonParser.Event)} method.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    @Override
    public void valueString(Context ctx, JsonParser.Event event) {
        simpleValue(ctx, event);
    }

    /**
     * De-serialize JSON Number value. 
     * Delegates processing to common {@code void simpleValue(Context, JsonParser.Event)} method.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    @Override
    public void valueNumber(Context ctx, JsonParser.Event event) {
        simpleValue(ctx, event);
    }

    /**
     * De-serialize JSON boolean value {@code true}
     * Delegates processing to common {@code void simpleValue(Context, JsonParser.Event)} method.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    @Override
    public void valueTrue(Context ctx, JsonParser.Event event) {
        simpleValue(ctx, event);
    }

    /**
     * De-serialize JSON boolean value {@code false}. 
     * Delegates processing to common {@code void simpleValue(Context, JsonParser.Event)} method.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    @Override
    public void valueFalse(Context ctx, JsonParser.Event event) {
        simpleValue(ctx, event);
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
                throw new JsonbException("Invalid JSON entry: " + event.name());
        }
        state = State.ENTRY_KEY;
    }

    /**
     * De-serialize JSON value {@code null}.
     *
     * @param ctx   parser context
     * @param event JSON parser token (event)
     */
    @Override
    public void valueNull(Context ctx, JsonParser.Event event) {
        switch (state) {
            case ENTRY_KEY_OBJECT:
            case ENTRY_VALUE_OBJECT:
                break;
            default:
                throw new JsonbException("Invalid JSON entry: " + event.name());
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
    @Override
    public void endArray(Context ctx, JsonParser.Event event) {
        switch (state) {
            case NEXT_ENTRY:
                ctx.finish();
                break;
            default:
                throw new JsonbException("Invalid JSON entry: " + event.name());
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
    @Override
    public void endObject(Context ctx, JsonParser.Event event) {
        switch (state) {
            case ENTRY_KEY:
                instance.put(key, value);
                break;
            default:
                throw new JsonbException("Invalid JSON entry: " + event.name());
        }
        state = State.NEXT_ENTRY;
    }

    /**
     * Deserialize key or value content using proper de-serializer.
     *
     * @param ctx parser context
     * @param contentType type of content to be de-serialized
     * @param event JSON parser token (event)
     * @return de-serialized key or value content to be stored into {@code Map}
     */
    @SuppressWarnings("unchecked")
    private <T> T deserializeContent(Context ctx, Type contentType, JsonParser.Event event) {
        final JsonbDeserializer<?> deserializer = ContainerDeserializer.newCollectionOrMapItem(this, contentType, ctx.getUnmarshallerContext().getJsonbContext(), event);
        return (T) deserializer.deserialize(ctx.getParser(), ctx.getUnmarshallerContext(), contentType);
    }

    /**
     * Clear internal Map.Entry storage before processing next entry.
     */
    private void clearMapEntry() {
        key = null;
        value = null;
    }

    /**
     * Move to the first event for current deserializer structure.
     *
     * @param parser JSON parser
     * @return first event
     */
    @Override
    public JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_ARRAY);
        return parser.getCurrentLevel();
    }

    /**
     * Return {@code Map} instance build by deserializer.
     *
     * @return container instance built by de-serializer
     */
    public Map<K,V> getInstance() {
        return instance;
    }

}
