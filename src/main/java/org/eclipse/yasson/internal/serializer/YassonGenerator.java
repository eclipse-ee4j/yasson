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
        return delegate.writeStartObject();
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        writeValidate("writeStartObject(String name)");
        level++;
        return delegate.writeStartObject(name);
    }

    @Override
    public JsonGenerator writeKey(String name) {
        writeValidate("writeKey(String name)");
        level++;
        return delegate.writeKey(name);
    }

    @Override
    public JsonGenerator writeStartArray() {
        writeValidate("writeStartArray()");
        level++;
        return delegate.writeStartArray();
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        writeValidate("writeStartArray(String name)");
        level++;
        return delegate.writeStartArray(name);
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        writeValidate("write(String name, JsonValue value)");
        return delegate.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, String value) {
        writeValidate("write(String name, String value)");
        return delegate.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        writeValidate("write(String name, BigInteger value)");
        return delegate.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        writeValidate("write(String name, BigDecimal value)");
        return delegate.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, int value) {
        writeValidate("write(String name, int value)");
        return delegate.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, long value) {
        writeValidate("write(String name, long value)");
        return delegate.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, double value) {
        writeValidate("write(String name, double value)");
        return delegate.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        writeValidate("write(String name, boolean value)");
        return delegate.write(name, value);
    }

    @Override
    public JsonGenerator writeNull(String name) {
        writeValidate("writeNull(String name)");
        return delegate.writeNull(name);
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
        return delegate.writeEnd();
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        writeValidate("write(JsonValue value)");
        level--;
        return delegate.write(value);
    }

    @Override
    public JsonGenerator write(String value) {
        writeValidate("write(String value)");
        level--;
        return delegate.write(value);
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        writeValidate("write(BigDecimal value)");
        level--;
        return delegate.write(value);
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        writeValidate("write(BigInteger value)");
        level--;
        return delegate.write(value);
    }

    @Override
    public JsonGenerator write(int value) {
        writeValidate("write(int value)");
        level--;
        return delegate.write(value);
    }

    @Override
    public JsonGenerator write(long value) {
        writeValidate("write(long value)");
        level--;
        return delegate.write(value);
    }

    @Override
    public JsonGenerator write(double value) {
        writeValidate("write(double value)");
        level--;
        return delegate.write(value);
    }

    @Override
    public JsonGenerator write(boolean value) {
        writeValidate("write(boolean value)");
        level--;
        return delegate.write(value);
    }

    @Override
    public JsonGenerator writeNull() {
        writeValidate("writeNull()");
        level--;
        return delegate.writeNull();
    }

    @Override
    public void close() {
        throw new JsonbException("Unsupported operation in user defined deserializer.");
    }

    @Override
    public void flush() {
        throw new JsonbException("Unsupported operation in user defined deserializer.");
    }

    private void writeValidate(String method) {
        if (level < 0) {
            throw new JsonbException(method + " cannot be called outside of the scope of user generator.");
        }
    }
}
