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

import java.util.Optional;
import java.util.UUID;

public class UUIDMapperClsBased extends MultilevelAdapterClass<UUID, String> {

    @Override
    public String adaptToJson(UUID obj) throws Exception {
        return Optional.ofNullable(obj).map(UUID::toString).orElse(null);
    }

    @Override
    public UUID adaptFromJson(String obj) throws Exception {
        return Optional.ofNullable(obj).map(UUID::fromString).orElse(null);
    }
}
