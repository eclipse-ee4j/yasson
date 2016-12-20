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
 * Deserialize {@link Integer}
 *
 * @author Roman Grigoriadi
 */
public class IntegerTypeDeserializer extends AbstractNumberDeserializer<Integer> {

    public IntegerTypeDeserializer(JsonBindingModel model) {
        super(Integer.class, model);
    }

    @Override
    protected Integer deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return deserializeForamtted(jsonValue, true, unmarshaller.getJsonbContext())
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
