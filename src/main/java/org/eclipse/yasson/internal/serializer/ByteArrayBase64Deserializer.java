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
import java.util.Base64;

import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserialize Base64 json string value into byte array.
 */
public class ByteArrayBase64Deserializer extends AbstractValueTypeDeserializer<byte[]> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public ByteArrayBase64Deserializer(Customization customization) {
        super(byte[].class, customization);
    }

    @Override
    protected byte[] deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return getDecoder(unmarshaller.getJsonbContext().getConfigProperties().getBinaryDataStrategy()).decode(jsonValue);
    }

    private Base64.Decoder getDecoder(String strategy) {
        switch (strategy) {
        case BinaryDataStrategy.BASE_64:
            return Base64.getDecoder();
        case BinaryDataStrategy.BASE_64_URL:
            return Base64.getUrlDecoder();
        default:
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Invalid strategy: " + strategy));
        }
    }
}
