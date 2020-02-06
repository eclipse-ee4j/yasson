/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.cdi;

import jakarta.json.bind.adapter.JsonbAdapter;

/**
 * @author Roman Grigoriadi
 */
public class NonCdiAdapter implements JsonbAdapter<String, Integer> {
    @Override
    public Integer adaptToJson(String obj) throws Exception {
        return Integer.valueOf(obj);
    }

    @Override
    public String adaptFromJson(Integer obj) throws Exception {
        return String.valueOf(obj);
    }
}
