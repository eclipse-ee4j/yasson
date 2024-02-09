/*
 * Copyright (c) 2019, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.jsonstructure;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonParserStreamCreator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Adapter for {@link JsonParser}, that reads a {@link JsonStructure} content tree instead of JSON text.
 * <p>
 * Yasson and jsonb API components are using {@link JsonParser} as its input API.
 * This adapter allows deserialization of {@link JsonStructure} into java content tree using same components
 * as when parsing JSON text.
 */
public class JsonStructureToParserAdapter implements JsonParser {

    private final Deque<JsonStructureIterator> iterators = new ArrayDeque<>();

    private final JsonStructure rootStructure;
    private final JsonProvider jsonProvider;

    //JsonParserImpl delivers the whole object - so we have to call next() before creation of the stream
    private final JsonParserStreamCreator streamCreator = new JsonParserStreamCreator(this, true, this::currentEvent, iterators::isEmpty);

    private Event currentEvent;

    /**
     * Creates new {@link JsonStructure} parser.
     *
     * @param structure    json structure
     * @param jsonProvider json provider for creation of {@link JsonValue} for keys
     */
    public JsonStructureToParserAdapter(JsonStructure structure, JsonProvider jsonProvider) {
        this.rootStructure = structure;
        this.jsonProvider = jsonProvider;
    }

    @Override
    public boolean hasNext() {
        JsonStructureIterator iterator = iterators.peek();
        return (iterator != null) && iterator.hasNext();
    }

    @Override
    public Event next() {
        if (iterators.isEmpty()) {
            currentEvent = pushIntoIterators(rootStructure, rootStructure instanceof JsonObject, Event.START_OBJECT,
                    rootStructure instanceof JsonArray, Event.START_ARRAY);
            return currentEvent;
        }
        JsonStructureIterator current = iterators.peek();
        currentEvent = current.next();
        pushIntoIterators(current.getValue(), currentEvent == Event.START_OBJECT, null, currentEvent == Event.START_ARRAY, null);
        if (currentEvent == Event.END_OBJECT || currentEvent == Event.END_ARRAY) {
            iterators.pop();
        }
        return currentEvent;
    }

    private Event pushIntoIterators(JsonValue value, boolean isObject, Event objectEvent, boolean isArray, Event arrayEvent) {
        if (isObject) {
            iterators.push(new JsonObjectIterator((JsonObject) value));
            return objectEvent;
        } else if (isArray) {
            iterators.push(new JsonArrayIterator((JsonArray) value));
            return arrayEvent;
        }
        return null;
    }

    @Override
    public Event currentEvent() {
        return currentEvent;
    }

    @Override
    public String getString() {
        if (currentEvent == null) {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getString() call with current event: null"));
        }

        switch (currentEvent) {
            case KEY_NAME:
            case VALUE_STRING:
            case VALUE_NUMBER:
                JsonStructureIterator iterator = iterators.peek();
                if (iterator == null) {
                    throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getString() call with empty internal stack"));
                }
                return iterator.getString();
            default:
                throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getString() call with current event: "
                        + currentEvent + "; should be in [KEY_NAME, VALUE_STRING, VALUE_NUMBER]"));
        }
    }

    @Override
    public boolean isIntegralNumber() {
        return getJsonNumberValue().isIntegral();
    }

    @Override
    public int getInt() {
        return getJsonNumberValue().intValueExact();
    }

    @Override
    public long getLong() {
        return getJsonNumberValue().longValueExact();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return getJsonNumberValue().bigDecimalValue();
    }

    @Override
    public JsonObject getObject() {
        JsonStructureIterator current = iterators.peek();
        if (currentEvent == Event.START_OBJECT) {
            //Remove child iterator as getObject() method contract says
            iterators.pop();
            currentEvent = Event.END_OBJECT;
            JsonValue value = current == null ? null : current.getValue();
            return value == null ? null : value.asJsonObject();
        } else {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getObject() Not at the beginning of an object"));
        }
    }

    private JsonNumber getJsonNumberValue() {
        JsonStructureIterator iterator = iterators.peek();
        if (iterator == null) {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Call of the number method on empty context"));
        }
        JsonValue value = iterator.getValue();
        if (value.getValueType() != JsonValue.ValueType.NUMBER) {
            throw iterator.createIncompatibleValueError();
        }
        return (JsonNumber) value;
    }

    @Override
    public JsonLocation getLocation() {
        throw new JsonbException("Operation not supported");
    }

    @Override
    public JsonValue getValue() {
        if (currentEvent == null) {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getValue() call with current event: null"));
        } else {
            JsonStructureIterator iterator = iterators.peek();
            if (iterator == null) {
                throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getValue() call empty internal stack"));
            } else {
                switch (currentEvent) {
                    case START_OBJECT:
                        return getObject();
                    case START_ARRAY:
                        return getArray();
                    case KEY_NAME:
                        return jsonProvider.createValue(iterator.getString());
                    case END_ARRAY:
                    case END_OBJECT:
                        throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getValue() call with current event: "
                                + currentEvent + "; should not be in [END_OBJECT, END_ARRAY]"));
                    default:
                        return iterator.getValue();
                }
            }
        }
    }

    @Override
    public JsonArray getArray() {
        if (currentEvent == Event.START_ARRAY) {
            //Remove child iterator as getArray() method contract says
            iterators.pop();
            currentEvent = Event.END_ARRAY;
            JsonStructureIterator current = iterators.peek();
            if (current == null) {
                throw new NoSuchElementException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "No more elements in JSON structure"));
            }
            return current.getValue().asJsonArray();
        } else {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getArray() not at the beginning of an array"));
        }
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        return streamCreator.getArrayStream();
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        return streamCreator.getObjectStream();
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        return streamCreator.getValueStream();
    }

    @Override
    public void skipArray() {
        skipJsonPart(iterator -> iterator instanceof JsonArrayIterator, Event.END_ARRAY);
    }

    @Override
    public void skipObject() {
        skipJsonPart(iterator -> iterator instanceof JsonObjectIterator, Event.END_OBJECT);
    }

    private void skipJsonPart(Predicate<JsonStructureIterator> predicate, Event newCurrentEvent) {
        Objects.requireNonNull(predicate);
        if (!iterators.isEmpty()) {
            JsonStructureIterator current = iterators.peek();
            if (predicate.test(current)) {
                iterators.pop();
                currentEvent = newCurrentEvent;
            }
        }
    }

    @Override
    public void close() {
        //noop
    }
}