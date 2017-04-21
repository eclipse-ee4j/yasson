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

import org.eclipse.yasson.internal.AbstractContainerSerializer;
import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.unmarshaller.CurrentItem;
import org.eclipse.yasson.model.ClassModel;
import org.eclipse.yasson.model.JsonBindingModel;
import org.eclipse.yasson.model.PropertyModel;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Type;

/**
 * Serializes arbitrary object by reading its properties.
 *
 * @author Roman Grigoriadi
 */
public class ObjectSerializer<T> extends AbstractContainerSerializer<T> {

    /**
     * Creates a new instance.
     *
     * @param builder Builder to initialize the instance.
     */
    public ObjectSerializer(SerializerBuilder builder) {
        super(builder);
    }

    /**
     * Creates a new instance.
     *
     * @param wrapper wrapped item
     * @param runtimeType class type
     * @param classModel model of the class
     * @param wrapperModel data binding model
     */
    public ObjectSerializer(CurrentItem<?> wrapper, Type runtimeType, ClassModel classModel, JsonBindingModel wrapperModel) {
        super(wrapper, runtimeType, classModel, wrapperModel);
    }

    @Override
    protected void serializeInternal(T object, JsonGenerator generator, SerializationContext ctx) {
        final PropertyModel[] allProperties = ((Marshaller) ctx).getMappingContext().getOrCreateClassModel(object.getClass()).getSortedProperties();
        for (PropertyModel model : allProperties) {
            marshallProperty(object, generator, ctx, model);
        }
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartObject();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartObject(key);
    }

    @SuppressWarnings("unchecked")
    private void marshallProperty(T object, JsonGenerator generator, SerializationContext ctx, PropertyModel propertyModel) {
        Marshaller marshaller = (Marshaller) ctx;
        final Object propertyValue = propertyModel.getValue(object);
        if (propertyValue == null) {
            if (propertyModel.getCustomization().isNillable()) {
                generator.writeNull(propertyModel.getWriteName());
            }
            return;
        }
        final JsonbSerializer<?> propertyCachedSerializer = propertyModel.getPropertySerializer();
        if (propertyCachedSerializer != null) {
            serializerCaptor(propertyCachedSerializer, propertyValue, generator, ctx);
            return;
        }
        Type genericType = ReflectionUtils.resolveType(this, propertyModel.getType());
        final JsonbSerializer<?> serializer = new SerializerBuilder(marshaller.getJsonbContext()).withWrapper(this)
                .withObjectClass(propertyValue.getClass()).withModel(propertyModel)
                .withType(genericType).build();
        serializerCaptor(serializer, propertyValue, generator, ctx);
    }
}
