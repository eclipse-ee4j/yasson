package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.model.JsonBindingModel;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Serializer for types using {@link java.time.format.DateTimeFormatter}.
 *
 * @author Roman Grigoriadi
 */
abstract class DateTimeFormatterSerializer<T extends TemporalAccessor> extends AbstractValueTypeSerializer<T> {

    DateTimeFormatterSerializer(JsonBindingModel model) {
        super(model);
    }

    protected DateTimeFormatter getFormatter(JsonbContext jsonbContext) {
        if (model != null && model.getCustomization() != null
                && model.getCustomization().getSerializeDateFormatter() != null) {
            final JsonbDateFormatter serializeDateFormatter = model.getCustomization().getSerializeDateFormatter();
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
