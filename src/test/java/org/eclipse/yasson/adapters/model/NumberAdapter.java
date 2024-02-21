/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.serializers.model.Counter;

import jakarta.json.bind.adapter.JsonbAdapter;

public class NumberAdapter implements JsonbAdapter<Number, String> {

    private final static Counter COUNTER = new Counter();

    public static Counter getCounter() {
        return COUNTER;
    }

    {
        COUNTER.add();
    }

    @Override
    public String adaptToJson(Number obj) throws Exception {
        return Integer.valueOf(((Integer)obj) + 1).toString();
    }

    @Override
    public Number adaptFromJson(String obj) throws Exception {
        return Integer.parseInt(obj) - 1;
    }
}
