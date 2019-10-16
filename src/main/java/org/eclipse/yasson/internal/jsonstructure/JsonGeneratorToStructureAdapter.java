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

package org.eclipse.yasson.internal.jsonstructure;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Adapter for {@link JsonGenerator}, that builds a {@link JsonStructure} content tree instead of JSON text.
 *
 * Yasson and jsonb API components are using {@link JsonGenerator} as its output API.
 * This adapter allows serialization of java content tree into {@link JsonStructure} using same components
 * as when generating JSON text.
 */
public class JsonGeneratorToStructureAdapter implements JsonGenerator {

    private final Deque<JsonStructureBuilder> builders;

    private JsonStructure root;

    private final JsonProvider provider;

    /**
     * Default constructor, jsonp builders are created internally.
     *
     * @param provider Cached json provider to create builders on.
     */
    public JsonGeneratorToStructureAdapter(JsonProvider provider) {
        this.builders = new ArrayDeque<>();
        this.provider = provider;
    }

    @Override
    public JsonGenerator writeStartObject() {
        builders.push(new JsonObjectBuilder(provider));
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        getJsonObjectBuilder(name).writeKey(name);
        builders.push(new JsonObjectBuilder(provider));
        return this;
    }

    @Override
    public JsonGenerator writeKey(String name) {
        getJsonObjectBuilder(name).writeKey(name);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        builders.push(new JsonArrayBuilder(provider));
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        getJsonObjectBuilder(name).writeKey(name);
        builders.push(new JsonArrayBuilder(provider));
        return this;
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        getJsonObjectBuilder(name).write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, String value) {
        getJsonObjectBuilder(name).write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        getJsonObjectBuilder(name).write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        getJsonObjectBuilder(name).write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, int value) {
        getJsonObjectBuilder(name).write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, long value) {
        getJsonObjectBuilder(name).write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, double value) {
        getJsonObjectBuilder(name).write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        getJsonObjectBuilder(name).write(name, value);
        return this;
    }

    private JsonObjectBuilder getJsonObjectBuilder(String keyName) {
        JsonStructureBuilder current = builders.peek();
        if (!(current instanceof JsonObjectBuilder)) {
            throw new JsonbException(Messages.getMessage(
                    MessageKeys.INTERNAL_ERROR, "Can't write key [" + keyName + "] into " + current.getClass()
                            + "because " + current.getClass() + " is not an instance of " + JsonObjectBuilder.class));
        }
        return (JsonObjectBuilder) current;
    }

    @Override
    public JsonGenerator writeNull(String name) {
        getJsonObjectBuilder(name).writeNull(name);
        return this;
    }

    @Override
    public JsonGenerator writeEnd() {
        JsonStructureBuilder builder = builders.pop();
        JsonStructure structure = builder.build();
        if (builders.isEmpty()) {
            this.root = structure;
        } else {
            builders.peek().put(structure);
        }
        return this;
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        builders.peek().write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String value) {
        builders.peek().write(value);
        return this;
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        builders.peek().write(value);
        return this;
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        builders.peek().write(value);
        return this;
    }

    @Override
    public JsonGenerator write(int value) {
        builders.peek().write(value);
        return this;
    }

    @Override
    public JsonGenerator write(long value) {
        builders.peek().write(value);
        return this;
    }

    @Override
    public JsonGenerator write(double value) {
        builders.peek().write(value);
        return this;
    }

    @Override
    public JsonGenerator write(boolean value) {
        builders.peek().write(value);
        return this;
    }

    @Override
    public JsonGenerator writeNull() {
        builders.peek().writeNull();
        return this;
    }

    @Override
    public void close() {
        //noop
    }

    @Override
    public void flush() {
        //noop
    }

    /**
     * Root structure wrapping all data.
     *
     * @return root JsonStructure.
     */
    public JsonStructure getRootStructure() {
        return root;
    }
}
