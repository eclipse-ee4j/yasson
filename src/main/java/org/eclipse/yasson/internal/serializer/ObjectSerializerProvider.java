package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.unmarshaller.CurrentItem;
import org.eclipse.yasson.model.ClassModel;
import org.eclipse.yasson.model.JsonBindingModel;
import org.eclipse.yasson.model.JsonbPropertyInfo;

import javax.json.bind.serializer.JsonbSerializer;
import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public class ObjectSerializerProvider implements ContainerSerializerProvider {

    @Override
    public JsonbSerializer<?> provideSerializer(JsonbPropertyInfo propertyInfo) {
        return new ObjectSerializer<>(propertyInfo.getWrapper(), propertyInfo.getRuntimeType(), propertyInfo.getClassModel(), propertyInfo.getJsonBindingModel());
    }
}
