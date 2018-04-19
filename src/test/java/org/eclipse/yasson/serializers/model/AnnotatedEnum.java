package org.eclipse.yasson.serializers.model;

import javax.json.bind.annotation.JsonbTypeSerializer;

@JsonbTypeSerializer(AnnotedEnumSerializer.class)
public enum AnnotatedEnum {

    SEXY,
    MONEY;
}
