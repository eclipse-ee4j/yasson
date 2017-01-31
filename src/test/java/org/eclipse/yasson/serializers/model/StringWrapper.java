`package org.eclipse.yasson.serializers.model;

import javax.json.bind.annotation.JsonbTypeSerializer;

/**
 * @author Roman Grigoriadi
 */
public class StringWrapper {

    @JsonbTypeSerializer(StringPaddingSerializer.class)
    public String strField;
}
