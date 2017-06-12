package org.eclipse.yasson.serializers.model;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * Causes {@link StackOverflowError} if recursive calls of user components are not checked by runtime.
 */
public class RecursiveDeserializer implements JsonbDeserializer<Box> {
    @Override
    public Box deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        return deserializationContext.deserialize(type, jsonParser);
    }
}
