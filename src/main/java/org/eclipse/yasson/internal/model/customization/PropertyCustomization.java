/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * Customization for a property of a class.
 *
 * @author Roman Grigoriadi
 */
public class PropertyCustomization extends CustomizationBase {

    private final String jsonReadName;

    private final String jsonWriteName;

    private final JsonbNumberFormatter serializeNumberFormatter;

    private final JsonbNumberFormatter deserializeNumberFormatter;

    private final JsonbDateFormatter serializeDateFormatter;

    private final JsonbDateFormatter deserializeDateFormatter;

    private boolean readTransient;

    private boolean writeTransient;

    /**
     * Copies properties from builder an creates immutable instance.
     * @param builder not null
     */
    public PropertyCustomization(PropertyCustomizationBuilder builder) {
        super(builder);
        this.jsonReadName = builder.getJsonReadName();
        this.jsonWriteName = builder.getJsonWriteName();
        this.serializeNumberFormatter = builder.getSerializeNumberFormatter();
        this.deserializeNumberFormatter = builder.getDeserializeNumberFormatter();
        this.serializeDateFormatter = builder.getSerializeDateFormatter();
        this.deserializeDateFormatter = builder.getDeserializeDateFormatter();
        this.readTransient = builder.isReadTransient();
        this.writeTransient = builder.isWriteTransient();
    }

    /**
     * Name if specified for property setter with {@link javax.json.bind.annotation.JsonbProperty}.
     * @return read name
     */
    public String getJsonReadName() {
        return jsonReadName;
    }

    /**
     * Name if specified for property getter with {@link javax.json.bind.annotation.JsonbProperty}.
     * @return write name
     */
    public String getJsonWriteName() {
        return jsonWriteName;
    }

    @Override
    public JsonbNumberFormatter getSerializeNumberFormatter() {
        return serializeNumberFormatter;
    }

    @Override
    public JsonbNumberFormatter getDeserializeNumberFormatter() {
        return deserializeNumberFormatter;
    }

    @Override
    public JsonbDateFormatter getSerializeDateFormatter() {
        return serializeDateFormatter;
    }

    @Override
    public JsonbDateFormatter getDeserializeDateFormatter() {
        return deserializeDateFormatter;
    }


    /**
     * The flag indicating whether the value of the underlying type/property should be processed during serialization process or not.
     *
     * @return  true indicates that the underlying type/property should be included in serialization process and false indicates it should not
     */
    public boolean isReadTransient() {
        return readTransient;
    }

    /**
     * The flag indicating whether the value of the underlying type/property should be processed during deserialization process or not.
     *
     * @return  true indicates that the underlying type/property should be included in deserialization process and false indicates it should not
     */
    public boolean isWriteTransient() {
        return writeTransient;
    }
}
