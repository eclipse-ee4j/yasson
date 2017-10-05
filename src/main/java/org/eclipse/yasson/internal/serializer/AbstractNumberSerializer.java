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

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.JsonBindingModel;
import org.eclipse.yasson.internal.model.customization.Customization;

import javax.json.stream.JsonGenerator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Consumer;

/**
 * Common serializer for numbers, using number format.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractNumberSerializer<T extends Number> extends AbstractValueTypeSerializer<T> {

    private final JsonbNumberFormatter formatter;

    /**
     * Creates an instance.
     *
     * @param model Binding model.
     */
    public AbstractNumberSerializer(JsonBindingModel model) {
        super(model);
        formatter = model.getCustomization() != null ?
                model.getCustomization().getSerializeNumberFormatter() : null;
    }

    /**
     * Serialize raw number when NumberFormat is not present.
     *
     * @param obj number
     * @param generator generator to use
     * @param key json key
     */
    protected abstract void serializeNonFormatted(T obj, JsonGenerator generator, String key);

    @Override
    protected void serialize(T obj, JsonGenerator generator, Marshaller marshaller) {
        if (formatter != null) {
            final NumberFormat format = NumberFormat.getInstance(marshaller.getJsonbContext().getConfigProperties().getLocale(formatter.getLocale()));
            ((DecimalFormat)format).applyPattern(formatter.getFormat());
            generator.write(format.format(obj));
        } else {
            serializeNonFormatted(obj, generator);
        }
    }

    /**
     * Serialize raw number when NumberFormat is not present.
     *
     * @param obj number
     * @param generator generator to use
     */
    protected abstract void serializeNonFormatted(T obj, JsonGenerator generator);

}
