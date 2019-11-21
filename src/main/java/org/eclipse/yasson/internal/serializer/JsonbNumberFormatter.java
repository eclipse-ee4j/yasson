/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

/**
 * Formatter for numbers.
 */
public class JsonbNumberFormatter {

    private final String format;

    private final String locale;

    /**
     * Construct with format string and locale.
     *
     * @param format formatter format
     * @param locale locale
     */
    public JsonbNumberFormatter(String format, String locale) {
        this.format = format;
        this.locale = locale;
    }

    /**
     * Format string to be used either by formatter.
     *
     * @return format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Locale to use with formatter.
     *
     * @return locale
     */
    public String getLocale() {
        return locale;
    }

}
