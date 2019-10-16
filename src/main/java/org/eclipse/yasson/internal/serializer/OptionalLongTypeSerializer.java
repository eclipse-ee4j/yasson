/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
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

import java.util.OptionalLong;

import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

import static org.eclipse.yasson.internal.serializer.OptionalObjectSerializer.handleEmpty;

/**
 * Serializer for {@link OptionalLong} type.
 */
public class OptionalLongTypeSerializer extends AbstractValueTypeSerializer<OptionalLong> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public OptionalLongTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected void serialize(OptionalLong obj, JsonGenerator generator, Marshaller marshaller) {
        if (!handleEmpty(obj, OptionalLong::isPresent, getCustomization(), generator, marshaller)) {
            generator.write(obj.getAsLong());
        }
    }
}
