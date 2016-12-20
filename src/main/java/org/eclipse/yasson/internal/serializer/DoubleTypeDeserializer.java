/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class DoubleTypeDeserializer extends AbstractNumberDeserializer<Double> {

    protected static final String POSITIVE_INFINITY = "POSITIVE_INFINITY";
    protected static final String NEGATIVE_INFINITY = "NEGATIVE_INFINITY";
    protected static final String NAN = "NaN";

    public DoubleTypeDeserializer(JsonBindingModel model) {
        super(Double.class, model);
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
        }
        return deserializeForamtted(jsonValue, false, unmarshaller.getJsonbContext())
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
