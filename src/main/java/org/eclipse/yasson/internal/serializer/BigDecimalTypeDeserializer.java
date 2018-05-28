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
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import javax.json.bind.JsonbException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Deserializer for {@link BigDecimal} type.
 *
 * @author David Kral
 */
public class BigDecimalTypeDeserializer extends AbstractNumberDeserializer<BigDecimal> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public BigDecimalTypeDeserializer(Customization customization) {
        super(BigDecimal.class, customization);
    }

    @Override
    public BigDecimal deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return deserializeFormatted(jsonValue, false, unmarshaller.getJsonbContext())
                .map(num -> new BigDecimal(num.toString()))
                .orElseGet(() -> {
                    try {
                        return new BigDecimal(jsonValue);
                    } catch (NumberFormatException e) {
                        throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR,
                                BigDecimal.class));
                    }
                });
    }
}
