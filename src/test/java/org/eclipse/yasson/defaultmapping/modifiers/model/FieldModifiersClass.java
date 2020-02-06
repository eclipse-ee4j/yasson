/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.modifiers.model;

/**
 * @author Roman Grigoriadi
 */
public class FieldModifiersClass {

    private final String finalString = "FINAL_STRING";

    private static final Long serialVersionUID = Long.MAX_VALUE;

    private static String staticString = "STATIC_STRING";

    private transient String transientString = "TRANSIENT_STRING";

    public String getFinalString() {
        return finalString;
    }

    public void setFinalString() {
        throw new IllegalStateException();
    }

    public static Long getSerialVersionUID() {
        throw new IllegalStateException();
    }

    public String getTransientString() {
        throw new IllegalStateException();
    }

    public void setTransientString(String transientString) {
        throw new IllegalStateException();
    }

    public static String getStaticString() {
        throw new IllegalStateException();
    }

    public static void setStaticString(String staticString) {
        throw new IllegalStateException();
    }
}
