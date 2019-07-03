package org.eclipse.yasson.internal;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

@Ignore
class AnnotationIntrospectorTestFixtures {

    public static interface ProvidesParameterRepresentation {
        Object[] asParameters();
    }

    private static final Map<String, Type> twoParameters(String name1, Type type1, String name2, Type type2) {
        Map<String, Type> parameters = new HashMap<>();
        parameters.put(name1, type1);
        parameters.put(name2, type2);
        return parameters;
    }

    public static Constructor<?>[] constructorsOf(Class<?> clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Constructor<?>[]>) clazz::getDeclaredConstructors);
    }

    public static class MissingAnnotationConstructor implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new MissingAnnotationConstructor("a string", Long.MAX_VALUE);
        }

        public MissingAnnotationConstructor(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "NoAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class JsonbCreatorAnnotatedConstructor implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new JsonbCreatorAnnotatedConstructor("a string", Long.MAX_VALUE);
        }

        @JsonbCreator
        public JsonbCreatorAnnotatedConstructor( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "JsonbCreatorAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class JsonbCreatorAnnotatedProtectedConstructor implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new JsonbCreatorAnnotatedProtectedConstructor("a string", Long.MAX_VALUE);
        }

        @JsonbCreator
        protected JsonbCreatorAnnotatedProtectedConstructor( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "JsonbCreatorAnnotatedProtectedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class JsonbCreatorAnnotatedFactoryMethod implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new JsonbCreatorAnnotatedFactoryMethod("text", Long.MIN_VALUE);
        }

        @JsonbCreator
        public static final JsonbCreatorAnnotatedFactoryMethod create( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            return new JsonbCreatorAnnotatedFactoryMethod(aString, aPrimitive);
        }

        private JsonbCreatorAnnotatedFactoryMethod(String string, long primitiv) {
            this.string = string;
            this.primitive = primitiv;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "JsonbCreatorAnnotatedFactoryMethod [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class TwoJsonbCreatorAnnotatedSpots implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new JsonbCreatorAnnotatedConstructor("", Long.valueOf(0));
        }

        @JsonbCreator
        public static final TwoJsonbCreatorAnnotatedSpots create( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            return new TwoJsonbCreatorAnnotatedSpots(aString, aPrimitive);
        }

        @JsonbCreator
        public TwoJsonbCreatorAnnotatedSpots( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "TwoJsonbCreatorAnnotatedSpots [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class ConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new ConstructorPropertiesAnnotation("  ", Long.valueOf(-12));
        }

        @ConstructorProperties({ "string", "primitive" })
        public ConstructorPropertiesAnnotation(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "ConstructorPropertiesAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class TwoConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new TwoConstructorPropertiesAnnotation("  ", Long.valueOf(-12));
        }

        @ConstructorProperties({ "string" })
        public TwoConstructorPropertiesAnnotation(String aString) {
            this(aString, 0L);
        }

        @ConstructorProperties({ "string", "primitive" })
        public TwoConstructorPropertiesAnnotation(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "TwoConstructorPropertiesAnnotation [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class JsonbCreatorAndConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new JsonbCreatorAndConstructorPropertiesAnnotation("", Long.valueOf(0));
        }

        @JsonbCreator
        public static final JsonbCreatorAndConstructorPropertiesAnnotation create( //
                @JsonbProperty("string") String aString, //
                @JsonbProperty("primitive") long aPrimitive) {
            return new JsonbCreatorAndConstructorPropertiesAnnotation(aString, aPrimitive);
        }

        @ConstructorProperties({ "string", "primitive" })
        public JsonbCreatorAndConstructorPropertiesAnnotation(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "JsonbCreatorAndConstructorPropertiesAnnotation [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class PublicNoArgAndAnnotatedPrivateConstructor implements ProvidesParameterRepresentation {
        private String string;
        private Long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new PublicNoArgAndAnnotatedPrivateConstructor("  ", Long.valueOf(-12));
        }

        public PublicNoArgAndAnnotatedPrivateConstructor() {
            super();
        }

        @ConstructorProperties({ "string", "primitive" })
        private PublicNoArgAndAnnotatedPrivateConstructor(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        public Long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Long primitive) {
            this.primitive = primitive;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "PublicNoArgAndAnnotatedPrivateConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class PublicNoArgAndAnnotatedPackageProtectedConstructor implements ProvidesParameterRepresentation {
        private String string;
        private Long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new PublicNoArgAndAnnotatedPackageProtectedConstructor("  ", Long.valueOf(-12));
        }

        public static final PublicNoArgAndAnnotatedPackageProtectedConstructor create(String aString, long aPrimitive) {
            return new PublicNoArgAndAnnotatedPackageProtectedConstructor(aString, aPrimitive);
        }

        public PublicNoArgAndAnnotatedPackageProtectedConstructor() {
            super();
        }

        @ConstructorProperties({ "string", "primitive" })
        PublicNoArgAndAnnotatedPackageProtectedConstructor(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        public Long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Long primitive) {
            this.primitive = primitive;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "PublicNoArgAndAnnotatedPackageProtectedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class PublicNoArgAndAnnotatedProtectedConstructor implements ProvidesParameterRepresentation {
        private String string;
        private Long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new PublicNoArgAndAnnotatedPackageProtectedConstructor("  ", Long.valueOf(-12));
        }

        public static final PublicNoArgAndAnnotatedProtectedConstructor create(String aString, long aPrimitive) {
            return new PublicNoArgAndAnnotatedProtectedConstructor(aString, aPrimitive);
        }

        @ConstructorProperties({ "string", "primitive" })
        protected PublicNoArgAndAnnotatedProtectedConstructor(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        public PublicNoArgAndAnnotatedProtectedConstructor() {
            super();
        }

        public Long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Long primitive) {
            this.primitive = primitive;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "PublicNoArgAndAnnotatedProtectedConstructor [string=" + string + ", primitive=" + primitive + "]";
        }
    }

    public static class MissingConstructorAnnotation implements ProvidesParameterRepresentation {
        private final String string;
        private final long primitive;

        public static final Map<String, Type> parameters() {
            return twoParameters("string", String.class, "primitive", long.class);
        }

        public static final ProvidesParameterRepresentation example() {
            return new MissingConstructorAnnotation("a string", Long.MAX_VALUE);
        }

        public MissingConstructorAnnotation(String aString, long aPrimitive) {
            this.string = aString;
            this.primitive = aPrimitive;
        }

        @Override
        public Object[] asParameters() {
            return new Object[] { string, primitive };
        }

        @Override
        public String toString() {
            return "MissingConstructorAnnotation [string=" + string + ", primitive=" + primitive + "]";
        }
    }
}