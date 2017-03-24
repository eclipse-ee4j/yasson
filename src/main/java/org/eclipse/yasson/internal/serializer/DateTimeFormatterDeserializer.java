package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.model.JsonBindingModel;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Serializer for types using {@link DateTimeFormatter}.
 *
 * @author Roman Grigoriadi
 */
abstract class DateTimeFormatterDeserializer<T extends TemporalAccessor> extends AbstractValueTypeDeserializer<T> {

    public DateTimeFormatterDeserializer(Class<T> clazz, JsonBindingModel model) {
        super(clazz, model);
    }

    protected DateTimeFormatter getFormatter(JsonbContext jsonbContext) {
        if (getModel() != null && getModel().getCustomization() != null
                && getModel().getCustomization().getDeserializeDateFormatter() != null) {
            final JsonbDateFormatter serializeDateFormatter = getModel().getCustomization().getSerializeDateFormatter();
            if (serializeDateFormatter.isDefault()) {
                return jsonbContext.getConfigProperties().isStrictIJson() ?
                        JsonbDateFormatter.IJSON_DATE_FORMATTER : getDefaultFormatter();
            }
            if (serializeDateFormatter.getDateTimeFormatter() != null) {
                return serializeDateFormatter.getDateTimeFormatter();
            }
        }

        return getDefaultFormatter();
    }

    /**
     * Default formatter for formatting java.time date objects.
     * @return formatter
     */
    protected abstract DateTimeFormatter getDefaultFormatter();
}
