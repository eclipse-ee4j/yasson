package org.eclipse.yasson.jsonstructure;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

public class InnerPojoDeserializer implements JsonbDeserializer<InnerPojo> {
    @Override
    public InnerPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        InnerPojo innerPojo = new InnerPojo();
        //KEY first
        parser.next();
        //VALUE
        parser.next();
        innerPojo.setInnerFirst(parser.getString());
        //KEY second
        parser.next();
        //VALUE
        parser.next();
        innerPojo.setInnerSecond(parser.getString());
        //END_OBJECT
        parser.next();
        return innerPojo;
    }
}
