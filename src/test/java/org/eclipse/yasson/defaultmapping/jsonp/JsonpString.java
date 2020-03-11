/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.jsonp;

import jakarta.json.JsonString;

/**
 * Test class implementing {@link JsonString} interface.
 *
 * @author Dmitry Kornilov
 */
public class JsonpString implements JsonString {

    private final String value;

    public JsonpString(String value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public CharSequence getChars() {
        return value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JsonString)) {
            return false;
        }
        JsonString other = (JsonString) obj;
        return getString().equals(other.getString());
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
