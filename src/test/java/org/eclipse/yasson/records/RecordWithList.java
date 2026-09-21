/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.records;

import jakarta.json.bind.annotation.JsonbCreator;

import java.util.List;

/**
 * Reproducer for https://github.com/eclipse-ee4j/yasson/issues/656.
 * A record with a {@code List} component and a compact {@code @JsonbCreator} constructor.
 */
public record RecordWithList(String name, List<RecordItem> items) {

    @JsonbCreator
    public RecordWithList {
    }
}
