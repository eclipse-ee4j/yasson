package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;

/**
 * Customization which is aware of bound components, such as adapters and (de)serializers.
 */
public interface ComponentBoundCustomization {

    /**
     * @return Adapter wrapper class with resolved generic information.
     */
    AdapterBinding getSerializeAdapterBinding();
    
    /**
     * @return Adapter wrapper class with resolved generic information.
     */
    AdapterBinding getDeserializeAdapterBinding();

    /**
     * Serializer wrapper with resolved generic info.
     *
     * @return serializer wrapper
     */
    SerializerBinding getSerializerBinding();

    /**
     * Deserializer wrapper with resolved generic info.
     *
     * @return deserializer wrapper
     */
    DeserializerBinding getDeserializerBinding();
}
