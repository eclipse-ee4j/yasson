/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.adapters.model;

import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.json.bind.adapter.JsonbAdapter;

public class FirstNameAdapter implements JsonbAdapter<String, JsonValue> {
    @Override
    public JsonValue adaptToJson(String firstName) {
        return Json.createValue(firstName.subSequence(0,1).toString());
    }
    @Override
    public String adaptFromJson(JsonValue json) {
        return json.toString();
    }
}
