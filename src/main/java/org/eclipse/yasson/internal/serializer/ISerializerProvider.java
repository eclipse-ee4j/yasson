package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.model.JsonBindingModel;

/**
 * Create instance of a serializer.
 *
 * @author Roman Grigoriadi
 */
public interface ISerializerProvider {

    /**
     * Provides new instance of serializer.
     * @param model model to use
     * @return deserializer
     */
    AbstractValueTypeSerializer<?> provideSerializer(JsonBindingModel model);
}
