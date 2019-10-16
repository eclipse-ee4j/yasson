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

import java.lang.reflect.Type;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link Integer} type.
 */
public class IntegerTypeDeserializer extends AbstractNumberDeserializer<Integer> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public IntegerTypeDeserializer(Customization customization) {
        super(Integer.class, customization);
    }

    @Override
    protected Integer deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return deserializeFormatted(jsonValue, true, unmarshaller.getJsonbContext())
                .map(num -> Integer.parseInt(num.toString()))
                .orElseGet(() -> {
                    try {
                        return Integer.parseInt(jsonValue);
                    } catch (NumberFormatException e) {
                        throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR,
                                                                     Integer.class));
                    }
                });
    }
}
