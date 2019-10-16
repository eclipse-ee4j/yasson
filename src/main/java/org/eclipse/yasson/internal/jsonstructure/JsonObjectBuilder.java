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

import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.spi.JsonProvider;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Builds {@link javax.json.JsonObject} delegates to {@link javax.json.JsonObjectBuilder}, caches key when
 * written without a value.
 */
class JsonObjectBuilder extends JsonStructureBuilder {

    private final javax.json.JsonObjectBuilder builder;

    private String nextKey;

    /**
     * Create instance with cached provider.
     *
     * @param provider Json provider to create JsonObjectBuilder on.
     */
    JsonObjectBuilder(JsonProvider provider) {
        this.builder = provider.createObjectBuilder();
    }

    @Override
    JsonStructure build() {
        return builder.build();
    }

    @Override
    void put(JsonStructure structure) {
        builder.add(getNextKey(), structure);
    }

    /**
     * Puts another {@link JsonStructure} into current using provided key.
     *
     * @param name      key to put JsonStructure under.
     * @param structure JsonStructure to put.
     */
    void put(String name, JsonStructure structure) {
        builder.add(name, structure);
    }

    @Override
    void write(JsonValue value) {
        builder.add(getNextKey(), value);
    }

    @Override
    void write(String value) {
        builder.add(getNextKey(), value);
    }

    @Override
    void write(BigDecimal value) {
        builder.add(getNextKey(), value);
    }

    @Override
    void write(BigInteger value) {
        builder.add(getNextKey(), value);
    }

    @Override
    void write(int value) {
        builder.add(getNextKey(), value);
    }

    @Override
    void write(long value) {
        builder.add(getNextKey(), value);
    }

    @Override
    void write(double value) {
        builder.add(getNextKey(), value);
    }

    @Override
    void write(boolean value) {
        builder.add(getNextKey(), value);
    }

    @Override
    void writeNull() {
        builder.addNull(getNextKey());
    }

    /**
     * Write a key-value pair into current {@link javax.json.JsonObject}.
     *
     * @param name  Key name to write value with.
     * @param value A value to write.
     */
    void write(String name, JsonValue value) {
        builder.add(name, value);
    }

    /**
     * Write a key-value pair into current {@link javax.json.JsonObject}.
     *
     * @param name  Key name to write value with.
     * @param value A value to write.
     */
    void write(String name, String value) {
        builder.add(name, value);
    }

    /**
     * Write a key-value pair into current {@link javax.json.JsonObject}.
     *
     * @param name  Key name to write value with.
     * @param value A value to write.
     */
    void write(String name, BigDecimal value) {
        builder.add(name, value);
    }

    /**
     * Write a key-value pair into current {@link javax.json.JsonObject}.
     *
     * @param name  Key name to write value with.
     * @param value A value to write.
     */
    void write(String name, BigInteger value) {
        builder.add(name, value);
    }

    /**
     * Write a key-value pair into current {@link javax.json.JsonObject}.
     *
     * @param name  Key name to write value with.
     * @param value A value to write.
     */
    void write(String name, int value) {
        builder.add(name, value);
    }

    /**
     * Write a key-value pair into current {@link javax.json.JsonObject}.
     *
     * @param name  Key name to write value with.
     * @param value A value to write.
     */
    void write(String name, long value) {
        builder.add(name, value);
    }

    /**
     * Write a key-value pair into current {@link javax.json.JsonObject}.
     *
     * @param name  Key name to write value with.
     * @param value A value to write.
     */
    void write(String name, double value) {
        builder.add(name, value);
    }

    /**
     * Write a key-value pair into current {@link javax.json.JsonObject}.
     *
     * @param name  Key name to write value with.
     * @param value A value to write.
     */
    void write(String name, boolean value) {
        builder.add(name, value);
    }

    /**
     * Write a null into current {@link javax.json.JsonObject} with a given key.
     *
     * @param name Key name to write null with.
     */
    void writeNull(String name) {
        builder.addNull(name);
    }

    /**
     * Store a key for putting next value into built {@link javax.json.JsonObject}.
     *
     * @param key Key to store.
     */
    void writeKey(String key) {
        this.nextKey = key;
    }

    private String getNextKey() {
        if (nextKey == null) {
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR,
                                                         "Can't write a value without key name"));
        }
        String key = nextKey;
        nextKey = null;
        return key;
    }
}
