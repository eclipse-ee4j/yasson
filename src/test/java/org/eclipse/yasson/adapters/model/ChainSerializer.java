package org.eclipse.yasson.adapters.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class ChainSerializer implements JsonbSerializer<Chain>{
    
    public static final String RECURSIVE_REFERENCE_ERROR = "There is a recursive reference";
    
    @Override
    public void serialize(Chain obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        if(obj.getHas() != null) {
            ctx.serialize("has", obj.getHas(), generator);
        }
        if(obj.getLinksTo() != null) {
            ctx.serialize("linksTo", obj.getLinksTo(), generator);
        }
        generator.write("name", obj.getName());
        generator.writeEnd();
    }
    
}
