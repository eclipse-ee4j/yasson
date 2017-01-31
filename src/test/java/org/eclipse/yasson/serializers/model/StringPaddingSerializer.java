package org.eclipse.yasson.serializers.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * @author Roman Grigoriadi
 */
public class StringPaddingSerializer implements JsonbSerializer<String> {
    @Override
    public void serialize(String obj, JsonGenerator generator, SerializationContext ctx) {
        generator.write("   "+obj);
    }
}
