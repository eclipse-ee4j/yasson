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

/**
 * Grouping interface for {@link javax.json.JsonObject} and {@link javax.json.JsonArray} generation.
 */
abstract class JsonStructureBuilder {

    /**
     * Build and get constructed {@link JsonStructure}.
     *
     * @return JsonStructure result.
     */
    abstract JsonStructure build();

    /**
     * Puts another {@link JsonStructure} into current. If current is {@link javax.json.JsonObject} than last written
     * key is used.
     *
     * @param structure
     */
    abstract void put(JsonStructure structure);

    /**
     * Write a value into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     *
     * @param value A value to write.
     */
    abstract void write(JsonValue value);

    /**
     * Write a value into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     *
     * @param value A value to write.
     */
    abstract void write(String value);

    /**
     * Write a value into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     *
     * @param value A value to write.
     */
    abstract void write(BigDecimal value);

    /**
     * Write a value into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     *
     * @param value A value to write.
     */
    abstract void write(BigInteger value);

    /**
     * Write a value into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     *
     * @param value A value to write.
     */
    abstract void write(int value);

    /**
     * Write a value into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     *
     * @param value A value to write.
     */
    abstract void write(long value);

    /**
     * Write a value into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     *
     * @param value A value to write.
     */
    abstract void write(double value);

    /**
     * Write a value into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     *
     * @param value A value to write.
     */
    abstract void write(boolean value);

    /**
     * Write null into current {@link JsonStructure}. If current is {@link javax.json.JsonObject}, last stored key
     * by {@link JsonObjectBuilder#writeKey(String)} is used.
     */
    abstract void writeNull();
}
