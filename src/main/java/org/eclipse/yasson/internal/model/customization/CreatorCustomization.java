package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * Customization for creator (constructor / factory methods) parameters.
 */
public class CreatorCustomization extends CustomizationBase {

    private JsonbNumberFormatter numberFormatter;

    private JsonbDateFormatter dateFormatter;

    private PropertyModel propertyModel;

    /**
     * Creates new creator customization instance.
     *
     * @param customization   builder of the customization
     * @param numberFormatter number formatter
     * @param dateFormatter   date formatter
     */
    public CreatorCustomization(CustomizationBuilder customization,
                                JsonbNumberFormatter numberFormatter,
                                JsonbDateFormatter dateFormatter) {
        super(customization);
        this.numberFormatter = numberFormatter;
        this.dateFormatter = dateFormatter;
    }

    @Override
    public JsonbNumberFormatter getSerializeNumberFormatter() {
        throw new UnsupportedOperationException("Serialization is not supported for creator parameters.");
    }

    @Override
    public JsonbNumberFormatter getDeserializeNumberFormatter() {
        if (numberFormatter != null) {
            return numberFormatter;
        } else if (propertyModel != null) {
            return propertyModel.getCustomization().getDeserializeNumberFormatter();
        }
        return null;
    }

    @Override
    public JsonbDateFormatter getSerializeDateFormatter() {
        throw new UnsupportedOperationException("Serialization is not supported for creator parameters.");
    }

    @Override
    public JsonbDateFormatter getDeserializeDateFormatter() {
        if (dateFormatter != null) {
            return dateFormatter;
        } else if (propertyModel != null) {
            return propertyModel.getCustomization().getDeserializeDateFormatter();
        }
        return null;
    }

    @Override
    public boolean isNillable() {
        throw new UnsupportedOperationException("Not supported for creator parameters.");
    }

    /**
     * Set property referenced model.
     *
     * @param propertyModel referenced property model
     */
    public void setPropertyModel(PropertyModel propertyModel) {
        this.propertyModel = propertyModel;
    }
}
