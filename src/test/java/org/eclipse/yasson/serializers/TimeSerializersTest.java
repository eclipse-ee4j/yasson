/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.serializers;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.Test;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public class TimeSerializersTest {

    private static final String EPOCH_DEFAULT = "\"1970-01-01T00:00:00Z[UTC]\"";
    private static final String EPOCH_CUSTOM =  "\"1970-01-01T00:00:00.000Z\"";

    private static final Jsonb JSONB_CUSTOM = JsonbBuilder.create(
        new JsonbConfig().withDateFormat(
            "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSSXXX", null));

    @Test
    public void serializeDateDefault() {
        Date date = new Date(0l);
        assertEquals(EPOCH_DEFAULT, defaultJsonb.toJson(date));
    }

    @Test
    public void serializeSqlTimestampDefault() {
        Timestamp ts = new Timestamp(0l);
        assertEquals(EPOCH_DEFAULT, defaultJsonb.toJson(ts));
    }

    @Test
    public void serializeDateCustom() {
        Date date = new Date(0l);
        assertEquals(EPOCH_CUSTOM, JSONB_CUSTOM.toJson(date));
    }

    @Test
    public void serializeSqlTimestampCustom() {
        Timestamp ts = new Timestamp(0l);
        assertEquals(EPOCH_CUSTOM, JSONB_CUSTOM.toJson(ts));
    }
}
