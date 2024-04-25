/*
 * Copyright (c) 2021, 2024 Oracle and/or its affiliates. All rights reserved.
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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.JsonParserStreamCreator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Yasson {@link YassonParser} parser wrapper.
 * <br>
 * Used for user defined deserializers. Does not allow deserializer to read outside the scope it should be used on.
 */
class YassonParser implements JsonParser {

    private final JsonParser delegate;
    private final DeserializationContextImpl context;
    private final JsonParserStreamCreator streamCreator;
    private final Deque<CurrentContext> contextStack = new ArrayDeque<>();

    YassonParser(JsonParser delegate, Event firstEvent, DeserializationContextImpl context) {
        this.delegate = delegate;
        this.context = context;
        CurrentContext currentContext = determineLevelValue(firstEvent);
        if (currentContext != null) {
            contextStack.push(currentContext);
        }
        streamCreator = new JsonParserStreamCreator(this, false, context::getLastValueEvent,
                () -> contextStack.size() == 1 && context.getLastValueEvent() == Event.START_OBJECT);
    }

    private CurrentContext determineLevelValue(Event firstEvent) {
        switch (firstEvent) {
        case START_ARRAY:
            return CurrentContext.ARRAY; //container start, there will be more events to come
        case START_OBJECT:
            return CurrentContext.OBJECT; //container start, there will be more events to come
        default:
            return null; //just this single value, do not allow reading more
        }
    }

    void skipRemaining() {
        while (hasNext()) {
            next();
        }
    }

    @Override
    public boolean hasNext() {
        if (contextStack.isEmpty()) {
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
            CurrentContext currentContext = determineLevelValue(next);
            if (currentContext != null) {
                contextStack.push(currentContext);
            }
            break;
        case END_OBJECT:
        case END_ARRAY:
            contextStack.pop();
            break;
        default:
            //no other changes needed
        }
        return next;
    }

    @Override
    public Event currentEvent() {
        return delegate.currentEvent();
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
        if (delegate.currentEvent() != Event.START_OBJECT) {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getObject() Not at the beginning of an object"));
        }
        validate();
        JsonObject jsonObject = delegate.getObject();
        contextStack.pop();
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
        if (delegate.currentEvent() != Event.START_ARRAY) {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getArray() Not at the beginning of an array"));
        }
        validate();
        JsonArray array = delegate.getArray();
        contextStack.pop();
        context.setLastValueEvent(Event.END_ARRAY);
        return array;
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        validate();
        return streamCreator.getArrayStream();
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        validate();
        return streamCreator.getObjectStream();
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        validate();
        return streamCreator.getValueStream();
    }

    @Override
    public void skipArray() {
        if (contextStack.peek() == CurrentContext.ARRAY) {
            delegate.skipArray();
            contextStack.pop();
            context.setLastValueEvent(Event.END_ARRAY);
        }
    }

    @Override
    public void skipObject() {
        if (contextStack.peek() == CurrentContext.OBJECT) {
            delegate.skipObject();
            contextStack.pop();
            context.setLastValueEvent(Event.END_OBJECT);
        }
    }

    @Override
    public void close() {
        delegate.close();
    }

    private void validate() {
        if (contextStack.isEmpty()) {
            throw new NoSuchElementException("There are no more elements available!");
        }
    }

    private enum CurrentContext {
        OBJECT,
        ARRAY
    }
}
