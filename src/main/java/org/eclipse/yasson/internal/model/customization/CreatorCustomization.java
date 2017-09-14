package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * Customization for creator (constructor / factory methods) parameters.
 */
public class CreatorCustomization implements Customization {

    private JsonbNumberFormatter numberFormatter;

    private JsonbDateFormatter dateFormatter;

    public CreatorCustomization(JsonbNumberFormatter numberFormatter, JsonbDateFormatter dateFormatter) {
        this.numberFormatter = numberFormatter;
        this.dateFormatter = dateFormatter;
    }

    @Override
    public JsonbNumberFormatter getSerializeNumberFormatter() {
        throw new UnsupportedOperationException("Serialization is not supported for creator parameters.");
    }

    @Override
    public JsonbNumberFormatter getDeserializeNumberFormatter() {
        return numberFormatter;
    }

    @Override
    public JsonbDateFormatter getSerializeDateFormatter() {
        throw new UnsupportedOperationException("Serialization is not supported for creator parameters.");
    }

    @Override
    public JsonbDateFormatter getDeserializeDateFormatter() {
        return dateFormatter;
    }

    @Override
    public boolean isNillable() {
        throw new UnsupportedOperationException("Not supported for creator parameters.");
    }

}
