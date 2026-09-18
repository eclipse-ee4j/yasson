/*
 * Copyright (c) 2021, 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

/**
 * Search for instance creator from other sources.
 * Mainly intended to add extensibility for different java versions and new features.
 */
public class ClassMultiReleaseExtension {

    private ClassMultiReleaseExtension() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    // Currently is unused - but could be used in the future.
}
