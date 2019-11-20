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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link String} type.
 */
public class StringTypeDeserializer extends AbstractValueTypeDeserializer<String> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public StringTypeDeserializer(Customization customization) {
        super(String.class, customization);
    }

    @Override
    protected String deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        if ((boolean) unmarshaller.getJsonbContext().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            try {
                String newString = new String(jsonValue.getBytes("UTF-8"), "UTF-8");
                if (!newString.equals(jsonValue)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.UNPAIRED_SURROGATE));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return jsonValue;
    }
}
