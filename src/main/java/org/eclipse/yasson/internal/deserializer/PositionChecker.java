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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;

import static jakarta.json.stream.JsonParser.Event;

/**
 * JSON document position checker.
 * <br>
 * Checks whether json parser is in expected state. If not it will try to skip to the next event, since
 * if user defined components are involved, it is possible to expect incorrect states in terms of the last expected events.
 * If this checker is still not in expected state, an exception is thrown.
 */
public class PositionChecker implements ModelDeserializer<JsonParser> {

    private static final Map<Event, Event> CLOSING_EVENTS = Map.of(Event.START_ARRAY, Event.END_ARRAY,
                                                                   Event.START_OBJECT, Event.END_OBJECT);

    private final Set<Event> expectedEvents;
    private final ModelDeserializer<JsonParser> delegate;
    private final Type rType;

    /**
     * Create new instance.
     *
     * @param delegate delegate which is call after the check
     * @param rType    runtime type
     * @param checker  bound group of events
     */
    public PositionChecker(ModelDeserializer<JsonParser> delegate, Type rType, Checker checker) {
        this(checker.events, delegate, rType);
    }

    /**
     * Create new instance.
     *
     * @param delegate delegate which is call after the check
     * @param rType    runtime type
     * @param events   customized checked events
     */
    public PositionChecker(ModelDeserializer<JsonParser> delegate, Type rType, Event... events) {
        this(Set.copyOf(Arrays.asList(events)), delegate, rType);
    }

    private PositionChecker(Set<Event> expectedEvents,
                            ModelDeserializer<JsonParser> delegate, Type rType) {
        this.expectedEvents = expectedEvents;
        this.delegate = delegate;
        this.rType = rType;
    }

    @Override
    public Object deserialize(JsonParser value, DeserializationContextImpl context) {
        Event original = context.getLastValueEvent();
        Event startEvent = original;
        if (!expectedEvents.contains(startEvent)) {
            startEvent = value.next();
            context.setLastValueEvent(startEvent);
            if (!expectedEvents.contains(startEvent)) {
                throw new JsonbException("Incorrect position for processing type: " + rType + ". "
                                                 + "Received event: " + original + " "
                                                 + "Allowed: " + expectedEvents);
            }
        }
        Object o = delegate.deserialize(value, context);
        if (CLOSING_EVENTS.containsKey(startEvent)
                && CLOSING_EVENTS.get(startEvent) != context.getLastValueEvent()) {
            throw new JsonbException("Incorrect parser position after processing of the type: " + rType + ". "
                                             + "Start event: " + startEvent + " "
                                             + "After processing event: " + context.getLastValueEvent());
        }
        return o;
    }

    @Override
    public String toString() {
        return "PositionChecker{"
                + "expectedEvents=" + expectedEvents
                + ", runtimeType=" + rType
                + '}';
    }

    /**
     * Grouped events according to whether it is container or value.
     */
    public enum Checker {

        /**
         * Value bound events.
         */
        VALUES(Event.VALUE_FALSE,
               Event.VALUE_TRUE,
               Event.VALUE_STRING,
               Event.VALUE_NUMBER,
               Event.VALUE_NULL),

        /**
         * Container bound events.
         */
        CONTAINER(Event.START_OBJECT,
                  Event.START_ARRAY);

        private final Set<Event> events;

        Checker(Event... events) {
            this.events = Set.of(events);
        }

        /**
         * Return events bound to the event group.
         *
         * @return set of bound events
         */
        public Set<Event> getEvents() {
            return events;
        }
    }

}
