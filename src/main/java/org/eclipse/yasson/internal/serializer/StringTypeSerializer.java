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

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonGenerator;
import java.io.UnsupportedEncodingException;

/**
 * Serializer for {@link String} type.
 * 
 * @author Roman Grigoriadi
 */
public class StringTypeSerializer extends AbstractValueTypeSerializer<String> {

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public StringTypeSerializer(JsonBindingModel model) {
        super(model);
    }

    private String toJson(String object, JsonbContext jsonbContext) {
        if ((boolean) jsonbContext.getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            try {
                String newString = new String(object.getBytes("UTF-8"), "UTF-8");
                if (!newString.equals(object)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.UNPAIRED_SURROGATE));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    @Override
    protected void serialize(String obj, JsonGenerator generator, Marshaller marshaller) {
        generator.write(toJson(obj, marshaller.getJsonbContext()));
    }
}
