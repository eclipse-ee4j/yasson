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
 * A record that exposes virtual (computed) attributes alongside its regular components.
 * <p>
 * <ul>
 *   <li>{@code type} and {@code color} are standard record components.</li>
 *   <li>{@code displayName()} is a virtual attribute: a zero-parameter, non-void method
 *       whose name does not match any component — it should appear in the serialized JSON.</li>
 *   <li>{@code hashCode()} and {@code toString()} are explicitly excluded by
 *       {@code isVirtualAccessorMethod} and must NOT appear in the JSON.</li>
 * </ul>
 */
public record RecordWithVirtualAttributes(String type, String color) {

    /** Virtual (computed) attribute — should be included in JSON output. */
    public String displayName() {
        return type + " (" + color + ")";
    }

    /** Returns a numeric virtual attribute — should be included in JSON output. */
    public int componentCount() {
        return 2;
    }
}
