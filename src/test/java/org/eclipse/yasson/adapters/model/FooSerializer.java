package org.eclipse.yasson.adapters.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class FooSerializer implements JsonbSerializer<Foo>{
    
    @Override
    public void serialize(Foo obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        generator.write("bar", obj.getBar());
        generator.writeEnd(); 
    }
    
}
