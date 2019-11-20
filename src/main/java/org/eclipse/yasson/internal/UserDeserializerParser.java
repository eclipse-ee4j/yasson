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

package org.eclipse.yasson.internal;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;

/**
 * Decorator for JSONP parser. Adds some checks for parser cursor manipulation methods.
 */
public class UserDeserializerParser implements JsonbParser {

    private final JsonbParser jsonbParser;

    /**
     * Remembered parser level, which is applied to user deserializer structure.
     */
    private final JsonbRiParser.LevelContext level;

    /**
     * Constructs an instance with parser and context.
     *
     * @param parser jsonb parser to decorate
     */
    public UserDeserializerParser(JsonbParser parser) {
        this.jsonbParser = parser;
        level = jsonbParser.getCurrentLevel();
    }

    /**
     * JsonParser in JSONB runtime is shared with user components, if user lefts cursor half way in progress
     * it must be advanced artificially to the end of JSON structure representing deserialized object.
     */
    public void advanceParserToEnd() {
        while (!level.isParsed() && jsonbParser.hasNext()) {
            next();
        }
    }

    @Override
    public boolean hasNext() {
        return !level.isParsed() && jsonbParser.hasNext();
    }

    @Override
    public Event next() {
        if (level.isParsed()) {
            throw new IllegalStateException("Parser level data inconsistent.");
        }
        return jsonbParser.next();
    }

    @Override
    public String getString() {
        return jsonbParser.getString();
    }

    @Override
    public boolean isIntegralNumber() {
        return jsonbParser.isIntegralNumber();
    }

    @Override
    public int getInt() {
        return jsonbParser.getInt();
    }

    @Override
    public long getLong() {
        return jsonbParser.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return jsonbParser.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return jsonbParser.getLocation();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    /**
     * Moves parser to required event, if current event is equal to required does nothing.
     *
     * @param event required event
     */
    @Override
    public void moveTo(Event event) {
        jsonbParser.moveTo(event);
    }

    /**
     * Moves parser cursor to any JSON value.
     */
    @Override
    public Event moveToValue() {
        return jsonbParser.moveToValue();
    }

    /**
     * Moves parser cursor to START_OBJECT or START_ARRAY.
     */
    @Override
    public Event moveToStartStructure() {
        return jsonbParser.moveToStartStructure();
    }

    /**
     * Current level of JsonbRiParser.
     *
     * @return current level
     */
    @Override
    public JsonbRiParser.LevelContext getCurrentLevel() {
        return jsonbParser.getCurrentLevel();
    }

    /**
     * Skips a value or a structure.
     * If current event is START_ARRAY or START_OBJECT, whole structure is skipped to end.
     */
    @Override
    public void skipJsonStructure() {
        jsonbParser.skipJsonStructure();
    }

    @Override
    public JsonObject getObject() {
        return jsonbParser.getObject();
    }

    @Override
    public JsonValue getValue() {
        return jsonbParser.getValue();
    }

    @Override
    public JsonArray getArray() {
        return jsonbParser.getArray();
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        return jsonbParser.getArrayStream();
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        return jsonbParser.getObjectStream();
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        return jsonbParser.getValueStream();
    }

    @Override
    public void skipArray() {
        jsonbParser.skipArray();
    }

    @Override
    public void skipObject() {
        jsonbParser.skipObject();
    }
}
