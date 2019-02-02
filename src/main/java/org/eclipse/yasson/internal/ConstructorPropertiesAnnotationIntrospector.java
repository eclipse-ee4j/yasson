package org.eclipse.yasson.internal;

import org.eclipse.yasson.internal.components.JsonbComponentInstanceCreator;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

class ConstructorPropertiesAnnotationIntrospector {

    private static final Logger LOG = Logger.getLogger(JsonbComponentInstanceCreator.class.getName());

    private final JsonbContext jsonbContext;
    private final AnnotationFinder<ConstructorProperties> constructorProperties;

    public static final ConstructorPropertiesAnnotationIntrospector forContext(JsonbContext jsonbContext) {
	return new ConstructorPropertiesAnnotationIntrospector(jsonbContext, AnnotationFinder.findAnnotation(ConstructorProperties.class));
    }

    protected ConstructorPropertiesAnnotationIntrospector(JsonbContext context, AnnotationFinder<ConstructorProperties> annotationFinder) {
	this.jsonbContext = context;
	this.constructorProperties = annotationFinder;
    }

    public JsonbCreator getCreator(Class<?> clazz) {
	JsonbCreator jsonbCreator = null;

	for (Constructor<?> constructor : declaredConstructorsIn(clazz)) {
	    ConstructorProperties annotation = constructorProperties.in(constructor.getDeclaredAnnotations());
	    if (annotation == null) {
		continue;
	    }
	    if (jsonbCreator != null) {
		// don't fail in this case, because it is perfectly allowed to have more than one
		// @ConstructorProperties-Annotation in general.
		// It is just undefined, which constructor to choose for JSON in this case.
		// The behavior should be the same (null), as if there is no ConstructorProperties-Annotation at all.
		LOG.warning(Messages.getMessage(MessageKeys.MULTIPLE_CONSTRUCTOR_PROPERTIES_CREATORS, clazz.getName()));
		return null;
	    }
	    jsonbCreator = createJsonbCreator(constructor, annotation);
	}
	return jsonbCreator;
    }

    private JsonbCreator createJsonbCreator(Executable executable, ConstructorProperties constructorProperties) {
	final Parameter[] parameters = executable.getParameters();

	CreatorModel[] creatorModels = new CreatorModel[parameters.length];
	for (int i = 0; i < parameters.length; i++) {
	    final Parameter parameter = parameters[i];
	    creatorModels[i] = new CreatorModel(constructorProperties.value()[i], parameter, jsonbContext);
	}
	return new JsonbCreator(executable, creatorModels);
    }

    private static Constructor<?>[] declaredConstructorsIn(Class<?> clazz) {
	return AccessController.doPrivileged((PrivilegedAction<Constructor<?>[]>) clazz::getDeclaredConstructors);
    }

    @Override
    public String toString() {
	return "ConstructorPropertiesAnnotationIntrospector [jsonbContext=" + jsonbContext + ", constructorProperties=" + constructorProperties + "]";
    }
}