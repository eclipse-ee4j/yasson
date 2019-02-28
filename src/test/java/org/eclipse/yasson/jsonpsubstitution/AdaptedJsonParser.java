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
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

public class AdaptedJsonParser implements JsonParser {

    /**
     * Adapts every parsed string value.
     */
    public interface StringValueAdapter {
        String adaptStringValue(String value);
    }

    private final StringValueAdapter adapter;

    private final JsonParser jsonParser;

    public AdaptedJsonParser(StringValueAdapter adapter, InputStream inputStream) {
        this.adapter = adapter;
        this.jsonParser = Json.createParser(inputStream);
    }

    @Override
    public boolean hasNext() {
        return jsonParser.hasNext();
    }

    @Override
    public Event next() {
        return jsonParser.next();
    }

    @Override
    public String getString() {
        return adapter.adaptStringValue(jsonParser.getString());
    }

    @Override
    public boolean isIntegralNumber() {
        return jsonParser.isIntegralNumber();
    }

    @Override
    public int getInt() {
        return jsonParser.getInt();
    }

    @Override
    public long getLong() {
        return jsonParser.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return jsonParser.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return jsonParser.getLocation();
    }

    @Override
    public JsonObject getObject() {
        return jsonParser.getObject();
    }

    @Override
    public JsonValue getValue() {
        return jsonParser.getValue();
    }

    @Override
    public JsonArray getArray() {
        return jsonParser.getArray();
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        return jsonParser.getArrayStream();
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        return jsonParser.getObjectStream();
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        return jsonParser.getValueStream();
    }

    @Override
    public void skipArray() {
        jsonParser.skipArray();
    }

    @Override
    public void skipObject() {
        jsonParser.skipObject();
    }

    @Override
    public void close() {
        jsonParser.close();
    }

}
