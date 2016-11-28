package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.model.JsonBindingModel;

/**
 * Creates instance of deserializer.
 *
 * @author Roman Grigoriadi
 */
public interface IDeserializerProvider {

    /**
     * Provides new instance of deserializer.
     * @param model model to use
     * @return deserializer
     */
    AbstractValueTypeDeserializer<?> provideDeserializer(JsonBindingModel model);
}
