package org.eclipse.yasson.serializers.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class AnnotedEnumSerializer implements JsonbSerializer<AnnotatedEnum> {

    @Override
    public void serialize(AnnotatedEnum obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        generator.write("valueField", "replaced enum value");
        generator.writeEnd();
    }

}
