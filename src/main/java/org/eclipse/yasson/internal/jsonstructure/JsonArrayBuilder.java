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

import javax.json.JsonArray;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

/**
 * Builds {@link JsonArray}. Delegates to {@link javax.json.JsonArrayBuilder}.
 */
class JsonArrayBuilder extends JsonStructureBuilder {

    private final javax.json.JsonArrayBuilder arrayBuilder;

    /**
     * Create instance with cached provider.
     *
     * @param provider Json provider to create JsonArrayBuilder on.
     */
    JsonArrayBuilder(JsonProvider provider) {
        this.arrayBuilder = provider.createArrayBuilder();
    }

    @Override
    JsonArray build() {
        return arrayBuilder.build();
    }

    @Override
    void write(JsonValue value) {
        arrayBuilder.add(value);
    }

    @Override
    void write(String value) {
        arrayBuilder.add(value);
    }

    @Override
    void write(BigDecimal value) {
        arrayBuilder.add(value);
    }

    @Override
    void write(BigInteger value) {
        arrayBuilder.add(value);
    }

    @Override
    void write(int value) {
        arrayBuilder.add(value);
    }

    @Override
    void write(long value) {
        arrayBuilder.add(value);
    }

    @Override
    void write(double value) {
        arrayBuilder.add(value);
    }

    @Override
    void write(boolean value) {
        arrayBuilder.add(value);
    }

    @Override
    void writeNull() {
        arrayBuilder.addNull();
    }

    @Override
    void put(JsonStructure structure) {
        arrayBuilder.add(structure);
    }
}
