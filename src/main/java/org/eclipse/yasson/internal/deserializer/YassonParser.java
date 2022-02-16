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

import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;

/**
 * Yasson {@link YassonParser} parser wrapper.
 * <br>
 * Used for user defined deserializers. Does not allow deserializer to read outside the scope it should be used on.
 */
class YassonParser implements JsonParser {

    private final JsonParser delegate;
    private final DeserializationContextImpl context;
    private int level;

    YassonParser(JsonParser delegate, Event firstEvent, DeserializationContextImpl context) {
        this.delegate = delegate;
        this.context = context;
        this.level = determineLevelValue(firstEvent);
    }

    private int determineLevelValue(Event firstEvent) {
        switch (firstEvent) {
        case START_ARRAY:
        case START_OBJECT:
            return 1; //container start, there will be more events to come
        default:
            return 0; //just this single value, do not allow reading more
        }
    }

    void skipRemaining() {
        while (hasNext()) {
            next();
        }
    }

    @Override
    public boolean hasNext() {
        if (level < 1) {
            return false;
        }
        return delegate.hasNext();
    }

    @Override
    public Event next() {
        validate();
        Event next = delegate.next();
        context.setLastValueEvent(next);
        switch (next) {
        case START_OBJECT:
        case START_ARRAY:
            level++;
            break;
        case END_OBJECT:
        case END_ARRAY:
            level--;
            break;
        default:
            //no other changes needed
        }
        return next;
    }

    @Override
    public String getString() {
        return delegate.getString();
    }

    @Override
    public boolean isIntegralNumber() {
        return delegate.isIntegralNumber();
    }

    @Override
    public int getInt() {
        return delegate.getInt();
    }

    @Override
    public long getLong() {
        return delegate.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return delegate.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return delegate.getLocation();
    }

    @Override
    public JsonObject getObject() {
        validate();
        level--;
        JsonObject jsonObject = delegate.getObject();
        context.setLastValueEvent(Event.END_OBJECT);
        return jsonObject;
    }

    @Override
    public JsonValue getValue() {
        final Event currentLevel = context.getLastValueEvent();
        switch (currentLevel) {
        case START_ARRAY:
            return getArray();
        case START_OBJECT:
            return getObject();
        default:
            return delegate.getValue();
        }
    }

    @Override
    public JsonArray getArray() {
        validate();
        level--;
        JsonArray array = delegate.getArray();
        context.setLastValueEvent(Event.END_ARRAY);
        return array;
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        validate();
        level--;
        return delegate.getArrayStream();
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        validate();
        level--;
        return delegate.getObjectStream();
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        validate();
        level--;
        return delegate.getValueStream();
    }

    @Override
    public void skipArray() {
        validate();
        level--;
        delegate.skipArray();
    }

    @Override
    public void skipObject() {
        validate();
        level--;
        delegate.skipObject();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    private void validate() {
        if (level < 1) {
            throw new NoSuchElementException("There are no more elements available!");
        }
    }
}
