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

package org.eclipse.yasson.internal.serializer;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;

/**
 * Yasson {@link JsonGenerator} generator wrapper.
 * <br>
 * Used for user defined serializers. Does not allow serializer to write outside the scope it should be used on.
 */
class YassonGenerator implements JsonGenerator {

    private final JsonGenerator delegate;
    private int level;

    YassonGenerator(JsonGenerator delegate) {
        this.delegate = delegate;
    }

    @Override
    public JsonGenerator writeStartObject() {
        writeValidate("writeStartObject()");
        level++;
        delegate.writeStartObject();
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        writeValidate("writeStartObject(String name)");
        level++;
        delegate.writeStartObject(name);
        return this;
    }

    @Override
    public JsonGenerator writeKey(String name) {
        writeValidate("writeKey(String name)");
        delegate.writeKey(name);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        writeValidate("writeStartArray()");
        level++;
        delegate.writeStartArray();
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        writeValidate("writeStartArray(String name)");
        level++;
        delegate.writeStartArray(name);
        return this;
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        writeValidate("write(String name, JsonValue value)");
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, String value) {
        writeValidate("write(String name, String value)");
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        writeValidate("write(String name, BigInteger value)");
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        writeValidate("write(String name, BigDecimal value)");
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, int value) {
        writeValidate("write(String name, int value)");
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, long value) {
        writeValidate("write(String name, long value)");
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, double value) {
        writeValidate("write(String name, double value)");
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        writeValidate("write(String name, boolean value)");
        delegate.write(name, value);
        return this;
    }

    @Override
    public JsonGenerator writeNull(String name) {
        writeValidate("writeNull(String name)");
        delegate.writeNull(name);
        return this;
    }

    @Override
    public JsonGenerator writeEnd() {
        level--;
        if (level < 0) {
            throw new JsonbException("writeEnd() cannot be called outside of the scope of user generator.");
        }
        if (level == 0) {
            level--; //if user has closed array or object and is on the same level he started. There is no more allowed writing.
        }
        delegate.writeEnd();
        return this;
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        writeValidate("write(JsonValue value)");
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(String value) {
        writeValidate("write(String value)");
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        writeValidate("write(BigDecimal value)");
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        writeValidate("write(BigInteger value)");
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(int value) {
        writeValidate("write(int value)");
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(long value) {
        writeValidate("write(long value)");
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(double value) {
        writeValidate("write(double value)");
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator write(boolean value) {
        writeValidate("write(boolean value)");
        delegate.write(value);
        return this;
    }

    @Override
    public JsonGenerator writeNull() {
        writeValidate("writeNull()");
        delegate.writeNull();
        return this;
    }

    @Override
    public void close() {
        throw new JsonbException("Unsupported operation in user defined serializer.");
    }

    @Override
    public void flush() {
        throw new JsonbException("Unsupported operation in user defined serializer.");
    }

    private void writeValidate(String method) {
        if (level < 0) {
            throw new JsonbException(method + " cannot be called outside of the scope of user generator.");
        }
    }
}
