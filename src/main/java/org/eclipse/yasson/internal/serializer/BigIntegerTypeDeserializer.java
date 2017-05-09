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
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 * Deserializer for {@link BigInteger} type.
 *
 * @author David Kral
 */
public class BigIntegerTypeDeserializer extends AbstractNumberDeserializer<BigInteger> {

    /**
     * Creates an instance.
     *
     * @param model Binding model.
     */
    public BigIntegerTypeDeserializer(JsonBindingModel model) {
        super(BigInteger.class, model);
    }

    @Override
    public BigInteger deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return deserializeFormatted(jsonValue, true, unmarshaller.getJsonbContext())
                .map(num -> new BigInteger(num.toString()))
                .orElseGet(() -> {
                    try {
                        return new BigInteger(jsonValue);
                    } catch (NumberFormatException e) {
                        throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR,
                                BigInteger.class));
                    }
                });
    }
}
