/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * Customization configuration for class or field.
 * Configuration parsed from annotation is put here.
 * Immutable.
 */
public interface Customization {

    /**
     * Number formatter for formatting numbers during serialization process. It could be the same formatter instance used for
     * deserialization
     * (returned by {@link #getDeserializeNumberFormatter()}
     *
     * @return number formatter
     */
    JsonbNumberFormatter getSerializeNumberFormatter();

    /**
     * Number formatter for formatting numbers during deserialization process. It could be the same formatter instance used for
     * serialization
     * (returned by {@link #getSerializeNumberFormatter()}
     *
     * @return number formatter
     */
    JsonbNumberFormatter getDeserializeNumberFormatter();

    /**
     * Date formatter for formatting date values during serialization process. It could be the same formatter instance used for
     * deserialization
     * (returned by {@link #getDeserializeDateFormatter()}. If not set, defaulted to <code>javax.json.bind.annotation
     * .JsonbDateFormat.DEFAULT_FORMAT.
     * </code>
     *
     * @return date formatter
     */
    JsonbDateFormatter getSerializeDateFormatter();

    /**
     * Date formatter for formatting date values during deserialization process. It could be the same formatter instance used
     * for serialization
     * (returned by {@link #getSerializeDateFormatter()}. If not set, defaulted to <code>javax.json.bind.annotation
     * .JsonbDateFormat.DEFAULT_FORMAT.
     * </code>
     *
     * @return date formatter
     */
    JsonbDateFormatter getDeserializeDateFormatter();

    /**
     * Returns true if <i>nillable</i> customization is present.
     *
     * @return True if <i>nillable</i> customization is present.
     */
    boolean isNillable();

}
