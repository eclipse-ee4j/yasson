/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson.jsonpsubstitution;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Test purpose generator appending a suffix key-value to end of object.
 */
public class SuffixJsonGenerator implements JsonGenerator {

    private final JsonGenerator generator;

    /**
     * Create key-value pair for each object at its end.
     */
    private final String objSuffix;

    public SuffixJsonGenerator(String objSuffix, OutputStream out) {
        this.objSuffix = objSuffix;
        this.generator = Json.createGenerator(out);
    }

    @Override
    public JsonGenerator writeStartObject() {
        return generator.writeStartObject();
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        return generator.writeStartObject(name);
    }

    @Override
    public JsonGenerator writeKey(String name) {
        return generator.writeKey(name);
    }

    @Override
    public JsonGenerator writeStartArray() {
        return generator.writeStartArray();
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        return generator.writeStartArray(name);
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        return generator.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, String value) {
        return generator.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        return generator.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        return generator.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, int value) {
        return generator.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, long value) {
        return generator.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, double value) {
        return generator.write(name, value);
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        return generator.write(name, value);
    }

    @Override
    public JsonGenerator writeNull(String name) {
        return generator.writeNull(name);
    }

    @Override
    public JsonGenerator writeEnd() {
        generator.write("suffix", objSuffix);
        return generator.writeEnd();
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        return generator.write(value);
    }

    @Override
    public JsonGenerator write(String value) {
        return generator.write(value);
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        return generator.write(value);
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        return generator.write(value);
    }

    @Override
    public JsonGenerator write(int value) {
        return generator.write(value);
    }

    @Override
    public JsonGenerator write(long value) {
        return generator.write(value);
    }

    @Override
    public JsonGenerator write(double value) {
        return generator.write(value);
    }

    @Override
    public JsonGenerator write(boolean value) {
        return generator.write(value);
    }

    @Override
    public JsonGenerator writeNull() {
        return generator.writeNull();
    }

    @Override
    public void close() {
        generator.close();
    }

    @Override
    public void flush() {
        generator.flush();
    }

}
