package org.eclipse.yasson.serializers.model;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

public class Author {

    public static final class FirstNameSerializer implements JsonbSerializer<String> {

        @Override
        public void serialize(String obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(obj.substring(0, 1));
        }
    }

    public static final class FirstNameDeserializer implements JsonbDeserializer<String> {
        @Override
        public String deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return "John";
        }
    }


    @JsonbTypeSerializer(FirstNameSerializer.class)
    @JsonbTypeDeserializer(FirstNameDeserializer.class)
    private String firstName;

    private String lastName;

    public Author() {
    }

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
