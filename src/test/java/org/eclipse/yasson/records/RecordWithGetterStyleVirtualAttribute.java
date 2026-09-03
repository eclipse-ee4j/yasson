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

/**
 * A record whose virtual accessor method uses a JavaBean-style {@code getX()} naming
 * convention.  Because this is a record (not a JavaBean), the {@code get} prefix must
 * NOT be stripped — the JSON key must be {@code "getDisplayName"}, not {@code "displayName"}.
 */
public record RecordWithGetterStyleVirtualAttribute(String type, String color) {

    /**
     * Virtual (computed) attribute with a JavaBean-style name.
     * The JSON key must be the full method name {@code "getDisplayName"},
     * not the bean-property name {@code "displayName"}.
     */
    public String getDisplayName() {
        return type + " (" + color + ")";
    }
}
