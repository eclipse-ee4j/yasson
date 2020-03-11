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

import jakarta.json.bind.annotation.JsonbTypeAdapter;
import java.util.UUID;


public class UUIDContainer {
    @JsonbTypeAdapter(UUIDMapperClsBased.class)
    private UUID uuidClsBased;

    @JsonbTypeAdapter(UUIDMapperIfcBased.class)
    private UUID uuidIfcBased;

    public UUID getUuidClsBased() {
        return uuidClsBased;
    }

    public void setUuidClsBased(UUID uuidClsBased) {
        this.uuidClsBased = uuidClsBased;
    }

    public UUID getUuidIfcBased() {
        return uuidIfcBased;
    }

    public void setUuidIfcBased(UUID uuidIfcBased) {
        this.uuidIfcBased = uuidIfcBased;
    }
}
