package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.unmarshaller.CurrentItem;
import org.eclipse.yasson.model.ClassModel;
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.serializer.JsonbSerializer;
import java.lang.reflect.Type;

/**
 * Provides container serializer instance.
 *
 * @author Roman Grigoriadi
 */
public interface ContainerSerializerProvider {

    JsonbSerializer<?> provideSerializer(CurrentItem<?> wrapper, Type runtimeType, ClassModel classModel, JsonBindingModel wrapperModel);
}
