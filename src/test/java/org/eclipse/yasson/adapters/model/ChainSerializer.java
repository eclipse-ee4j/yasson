package org.eclipse.yasson.adapters.model;

import java.util.HashSet;
import java.util.Set;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class ChainSerializer implements JsonbSerializer<Chain>{
    
    public static final String RECURSIVE_REFERENCE_ERROR = "There is a recursive reference";
    
    @Override
    public void serialize(Chain obj, JsonGenerator generator, SerializationContext ctx) {
        Set<Chain> processed = new HashSet<>();
        serializeChain(processed, obj, generator, null);
    }
    
    private void serializeHas(Chain obj, JsonGenerator generator) {
        if(obj.getHas() != null) {
            generator.writeStartObject("has").write("bar", obj.getHas().getBar()).writeEnd();
        }
    }
    
    private void serializeChain(Set<Chain> processed, Chain obj, JsonGenerator generator, String name) {
        if(obj != null) {
            if(processed.add(obj)) {
                if(name == null) {
                    generator.writeStartObject();
                } else {
                    generator.writeStartObject(name);
                }
                serializeHas(obj, generator);
                serializeChain(processed, obj.getLinksTo(), generator, "linksTo");
                generator.write("name", obj.getName());
                generator.writeEnd();
            } else {
                throw new RuntimeException(RECURSIVE_REFERENCE_ERROR);
            }
        }
    }
    
}
