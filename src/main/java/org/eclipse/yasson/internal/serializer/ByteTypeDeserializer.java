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
public class ByteTypeDeserializer extends AbstractNumberDeserializer<Byte> {

    public ByteTypeDeserializer(JsonBindingModel model) {
        super(Byte.class, model);
    }

    @Override
    protected Byte deserialize(String value, Unmarshaller unmarshaller, Type rtType) {
        return deserializeForamtted(value, true, unmarshaller.getJsonbContext())
                .map(num -> Byte.parseByte(num.toString()))
                .orElseGet(() -> {
                    try {
                        return Byte.parseByte(value);
                    } catch (NumberFormatException e) {
                        throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR, Byte.class));
                    }
                });
    }
}
