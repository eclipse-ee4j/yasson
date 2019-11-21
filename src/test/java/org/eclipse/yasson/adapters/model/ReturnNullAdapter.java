/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
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

import javax.json.bind.adapter.JsonbAdapter;

public class ReturnNullAdapter implements JsonbAdapter<Number, String> {
    @Override
    public String adaptToJson(Number obj) throws Exception {
        return null;
    }

    @Override
    public Number adaptFromJson(String obj) throws Exception {
        return null;
    }
}
