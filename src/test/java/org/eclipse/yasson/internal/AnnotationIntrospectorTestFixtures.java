package org.eclipse.yasson.internal;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
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

    public static class ObjectWithoutAnnotatedConstructor implements ProvidesParameterRepresentation {
	private final String string;
	private final long primitive;

	public static final Map<String, Type> parameters() {
	    return twoParameters("string", String.class, "primitive", long.class);
	}

	public static final ProvidesParameterRepresentation example() {
	    return new ObjectWithoutAnnotatedConstructor("a string", Long.MAX_VALUE);
	}

	@JsonbCreator
	public ObjectWithoutAnnotatedConstructor( //
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
	    return "ObjectWithNotAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
	}
    }

    public static class ObjectWithJsonbCreatorAnnotatedConstructor implements ProvidesParameterRepresentation {
	private final String string;
	private final long primitive;

	public static final Map<String, Type> parameters() {
	    return twoParameters("string", String.class, "primitive", long.class);
	}

	public static final ProvidesParameterRepresentation example() {
	    return new ObjectWithJsonbCreatorAnnotatedConstructor("a string", Long.MAX_VALUE);
	}

	@JsonbCreator
	public ObjectWithJsonbCreatorAnnotatedConstructor( //
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
	    return "ObjectWithJsonbCreatorAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
	}
    }

    public static class ObjectWithJsonbCreatorAnnotatedFactoryMethod implements ProvidesParameterRepresentation {
	private final String string;
	private final long primitive;

	public static final Map<String, Type> parameters() {
	    return twoParameters("string", String.class, "primitive", long.class);
	}

	public static final ProvidesParameterRepresentation example() {
	    return new ObjectWithJsonbCreatorAnnotatedFactoryMethod("text", Long.MIN_VALUE);
	}

	@JsonbCreator
	public static final ObjectWithJsonbCreatorAnnotatedFactoryMethod create( //
		@JsonbProperty("string") String aString, //
		@JsonbProperty("primitive") long aPrimitive) {
	    return new ObjectWithJsonbCreatorAnnotatedFactoryMethod(aString, aPrimitive);
	}

	private ObjectWithJsonbCreatorAnnotatedFactoryMethod(String string, long primitiv) {
	    this.string = string;
	    this.primitive = primitiv;
	}

	@Override
	public Object[] asParameters() {
	    return new Object[] { string, primitive };
	}

	@Override
	public String toString() {
	    return "ObjectWithJsonbCreatorAnnotatedFactoryMethod [string=" + string + ", primitive=" + primitive + "]";
	}
    }

    public static class ObjectWithTwoJsonbCreatorAnnotatedSpots implements ProvidesParameterRepresentation {
	private final String string;
	private final long primitive;

	public static final Map<String, Type> parameters() {
	    return twoParameters("string", String.class, "primitive", long.class);
	}

	public static final ProvidesParameterRepresentation example() {
	    return new ObjectWithJsonbCreatorAnnotatedConstructor("", Long.valueOf(0));
	}

	@JsonbCreator
	public static final ObjectWithTwoJsonbCreatorAnnotatedSpots create( //
		@JsonbProperty("string") String aString, //
		@JsonbProperty("primitive") long aPrimitive) {
	    return new ObjectWithTwoJsonbCreatorAnnotatedSpots(aString, aPrimitive);
	}

	@JsonbCreator
	public ObjectWithTwoJsonbCreatorAnnotatedSpots( //
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
	    return "ObjectWithTwoJsonbCreatorAnnotatedSpots [string=" + string + ", primitive=" + primitive + "]";
	}
    }

    public static class ObjectWithConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
	private final String string;
	private final long primitive;

	public static final Map<String, Type> parameters() {
	    return twoParameters("string", String.class, "primitive", long.class);
	}

	public static final ProvidesParameterRepresentation example() {
	    return new ObjectWithConstructorPropertiesAnnotation("  ", Long.valueOf(-12));
	}

	@ConstructorProperties({ "string", "primitive" })
	public ObjectWithConstructorPropertiesAnnotation(String aString, long aPrimitive) {
	    this.string = aString;
	    this.primitive = aPrimitive;
	}

	@Override
	public Object[] asParameters() {
	    return new Object[] { string, primitive };
	}

	@Override
	public String toString() {
	    return "ObjectWithConstructorPropertiesAnnotatedConstructor [string=" + string + ", primitive=" + primitive + "]";
	}
    }

    public static class ObjectWithTwoConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
	private final String string;
	private final long primitive;

	public static final Map<String, Type> parameters() {
	    return twoParameters("string", String.class, "primitive", long.class);
	}

	public static final ProvidesParameterRepresentation example() {
	    return new ObjectWithTwoConstructorPropertiesAnnotation("  ", Long.valueOf(-12));
	}

	@ConstructorProperties({ "string" })
	public ObjectWithTwoConstructorPropertiesAnnotation(String aString) {
	    this(aString, 0L);
	}

	@ConstructorProperties({ "string", "primitive" })
	public ObjectWithTwoConstructorPropertiesAnnotation(String aString, long aPrimitive) {
	    this.string = aString;
	    this.primitive = aPrimitive;
	}

	@Override
	public Object[] asParameters() {
	    return new Object[] { string, primitive };
	}

	@Override
	public String toString() {
	    return "ObjectWithTwoConstructorPropertiesAnnotation [string=" + string + ", primitive=" + primitive + "]";
	}
    }

    public static class ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation implements ProvidesParameterRepresentation {
	private final String string;
	private final long primitive;

	public static final Map<String, Type> parameters() {
	    return twoParameters("string", String.class, "primitive", long.class);
	}

	public static final ProvidesParameterRepresentation example() {
	    return new ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation("", Long.valueOf(0));
	}

	@JsonbCreator
	public static final ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation create( //
		@JsonbProperty("string") String aString, //
		@JsonbProperty("primitive") long aPrimitive) {
	    return new ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation(aString, aPrimitive);
	}

	@ConstructorProperties({ "string", "primitive" })
	public ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation(String aString, long aPrimitive) {
	    this.string = aString;
	    this.primitive = aPrimitive;
	}

	@Override
	public Object[] asParameters() {
	    return new Object[] { string, primitive };
	}

	@Override
	public String toString() {
	    return "ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation [string=" + string + ", primitive=" + primitive + "]";
	}
    }
}