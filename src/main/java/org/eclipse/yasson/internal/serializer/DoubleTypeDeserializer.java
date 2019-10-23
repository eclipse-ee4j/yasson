/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Type;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link Double} type.
 */
public class DoubleTypeDeserializer extends AbstractNumberDeserializer<Double> {

    private static final String POSITIVE_INFINITY = "POSITIVE_INFINITY";
    private static final String NEGATIVE_INFINITY = "NEGATIVE_INFINITY";
    private static final String NAN = "NaN";

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    DoubleTypeDeserializer(Customization customization) {
        super(Double.class, customization);
    }

    @Override
    protected Double deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        switch (jsonValue) {
        case NAN:
            return Double.NaN;
        case POSITIVE_INFINITY:
            return Double.POSITIVE_INFINITY;
        case NEGATIVE_INFINITY:
            return Double.NEGATIVE_INFINITY;
        default:
            return deserializeFormatted(jsonValue, false, unmarshaller.getJsonbContext())
                    .map(num -> Double.parseDouble(num.toString()))
                    .orElseGet(() -> {
                        try {
                            return Double.parseDouble(jsonValue);
                        } catch (NumberFormatException e) {
                            throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR,
                                                                         Double.class));
                        }
                    });
        }
    }
}
