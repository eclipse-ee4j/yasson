package org.eclipse.yasson.jsonstructure;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class InnerPojoSerializer implements JsonbSerializer<InnerPojo> {

    @Override
    public void serialize(InnerPojo obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        generator.write("first", obj.getInnerFirst());
        generator.write("second", obj.getInnerSecond());
        generator.writeEnd();
    }
}
