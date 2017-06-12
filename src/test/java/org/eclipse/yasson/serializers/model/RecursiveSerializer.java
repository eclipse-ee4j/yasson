package org.eclipse.yasson.serializers.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Causes {@link StackOverflowError} if recursive calls of user components are not checked by runtime.
 */
public class RecursiveSerializer implements JsonbSerializer<Box> {
    @Override
    public void serialize(Box box, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        jsonGenerator.writeStartObject();
        serializationContext.serialize("boxFieldName", box, jsonGenerator);
        jsonGenerator.writeEnd();
    }
}
