/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import javax.json.JsonValue;

/**
 * Common serializer functionality.
 *
 * @param <T> Type to serialize.
 */
public abstract class AbstractJsonpSerializer<T extends JsonValue> extends AbstractContainerSerializer<T> {

    /**
     * Creates new instance of jsonp serializer.
     *
     * @param builder serializer builder
     */
    protected AbstractJsonpSerializer(SerializerBuilder builder) {
        super(builder);
    }

}
