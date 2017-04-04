/*******************************************************************************
 * Copyright (c) 2015, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.internal;

import org.eclipse.yasson.model.ClassModel;
import org.eclipse.yasson.model.JsonbAnnotatedElement;
import org.eclipse.yasson.model.Property;
import org.eclipse.yasson.model.PropertyModel;
import org.eclipse.yasson.model.ReflectionPropagation;

import javax.json.bind.JsonbException;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created a class internal model.
 *
 * @author Dmitry Kornilov
 */
class ClassParser {

    public static final String IS_PREFIX = "is";

    public static final String GET_PREFIX = "get";

    public static final String SET_PREFIX = "set";

    private final JsonbContext jsonbContext;

    ClassParser(JsonbContext jsonbContext) {
        this.jsonbContext = jsonbContext;
    }

    /**
     * Parse class fields and getters setters. Merge to java bean like properties.
     */
    public void parseProperties(ClassModel classModel, JsonbAnnotatedElement<Class<?>> classElement) {

        final Map<String, Property> classProperties = new HashMap<>();
        parseFields(classElement, classProperties);
        parseClassAndInterfaceMethods(classElement, classProperties);

        //add sorted properties from parent, if they are not overridden in current class
        final List<PropertyModel> sortedProperties = getSortedParentProperties(classModel, classElement, classProperties);
        //sort and add properties from current class
        sortedProperties.addAll(jsonbContext.getConfigProperties().getPropertyOrdering().orderProperties(classProperties, classModel, jsonbContext));

        checkPropertyNameClash(sortedProperties, classModel.getType());
        classModel.setProperties(sortedProperties);

    }

    private void parseClassAndInterfaceMethods(JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        Class<?> concreteClass = classElement.getElement();
        parseMethods(concreteClass, classElement, classProperties);
        for (Class<?> ifc : jsonbContext.getAnnotationIntrospector().collectInterfaces(concreteClass)) {
            parseIfaceMethodAnnotations(ifc, classProperties);
        }
    }

    private void parseIfaceMethodAnnotations(Class<?> ifc, Map<String, Property> classProperties) {
        for(Method method : ifc.getDeclaredMethods()) {
            final String methodName = method.getName();
            if (!isPropertyMethod(methodName)) {
                continue;
            }
            String propertyName = toPropertyMethod(methodName);
            final Property property = classProperties.get(propertyName);
            if (property == null) {
                //May happen for classes which both extend a class with some method and implement interface with same method.
                continue;
            }
            JsonbAnnotatedElement<Method> methodElement = isGetter(methodName) ?
                    property.getGetterElement() : property.getSetterElement();
            //Only push iface annotations if not overridden on impl classes
            for (Annotation ann : method.getDeclaredAnnotations()) {
                if (methodElement.getAnnotation(ann.annotationType()) == null) {
                    methodElement.putAnnotation(ann);
                }
            }
        }
    }

    private void parseMethods(Class<?> clazz, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        for (Method method : clazz.getDeclaredMethods()) {
            String name = method.getName();
            if (!isPropertyMethod(name)) {
                continue;
            }
            final String propertyName = toPropertyMethod(name);

            Property property = classProperties.get(propertyName);
            if (property == null) {
                property= new Property(propertyName, classElement);
                classProperties.put(propertyName, property);
            }

            if (isSetter(name)) {
                property.setSetter(method);
            } else {
                property.setGetter(method);
            }
        }
    }

    private boolean isGetter(String methodName) {
        return methodName.startsWith(GET_PREFIX) || methodName.startsWith(IS_PREFIX);
    }

    private boolean isSetter(String methodName) {
        return methodName.startsWith(SET_PREFIX);
    }

    private String toPropertyMethod(String name) {
        return Introspector.decapitalize(name.substring(name.startsWith(IS_PREFIX) ? 2 : 3, name.length()));
    }

    private boolean isPropertyMethod(String name) {
        return name.startsWith(GET_PREFIX) || name.startsWith(SET_PREFIX) || name.startsWith(IS_PREFIX);
    }

    private void parseFields(JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        for (Field field : classElement.getElement().getDeclaredFields()) {
            final String name = field.getName();
            if (field.isSynthetic()) {
                continue;
            }
            final Property property = new Property(name, classElement);
            property.setField(field);
            classProperties.put(name, property);
        }
    }

    private void checkPropertyNameClash(List<PropertyModel> collectedProperties, Class cls) {
        final List<PropertyModel> checkedProperties = new ArrayList<>();
        for (PropertyModel collectedPropertyModel : collectedProperties) {
            for (PropertyModel checkedPropertyModel : checkedProperties) {

                if (checkedPropertyModel.getReadName().equals(collectedPropertyModel.getReadName()) ||
                        checkedPropertyModel.getWriteName().equals(collectedPropertyModel.getWriteName())) {
                    throw new JsonbException(String.format("Property %s clashes with property %s by read or write name in class %s.",
                            checkedPropertyModel.getPropertyName(), collectedPropertyModel.getPropertyName(), cls.getName()));
                }
            }
            checkedProperties.add(collectedPropertyModel);
        }
    }

    /**
     * Merges current class properties with parent class properties.
     * If javabean property is declared in more than one inheritance levels,
     * merge field, getters and setters of that property.
     *
     * For example BaseClass contains field foo and getter getFoo. In BaseExtensions there is a setter setFoo.
     * All three will be merged for BaseExtension.
     *
     * Such property is sorted based on where its getter or field is located.
     */
    private  List<PropertyModel> getSortedParentProperties(ClassModel classModel, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        List<PropertyModel> sortedProperties = new ArrayList<>();
        //Pull properties from parent
        if (classModel.getParentClassModel() != null) {
            for (PropertyModel parentProp : classModel.getParentClassModel().getSortedProperties()) {
                final Property current = classProperties.get(parentProp.getPropertyName());
                //don't replace overridden properties
                if (current == null) {
                    sortedProperties.add(parentProp);
                } else {
                    //merge
                    final Property merged = mergeProperty(current, parentProp, classElement);
                    ReflectionPropagation propagation = new ReflectionPropagation(current, jsonbContext);
                    if (propagation.isReadable()) {
                        classProperties.replace(current.getName(), merged);
                    } else {
                        sortedProperties.add(new PropertyModel(classModel, merged, jsonbContext));
                        classProperties.remove(current.getName());
                    }

                }
            }
        }
        return sortedProperties;
    }

    private Property mergeProperty(Property current, PropertyModel parentProp, JsonbAnnotatedElement<Class<?>> classElement) {
        Field field = current.getField() != null
                ? current.getField() : parentProp.getPropagation().getField();
        Method getter = current.getGetter() != null
                ? current.getGetter() : parentProp.getPropagation().getGetter();
        Method setter = current.getSetter() != null
                ? current.getSetter() : parentProp.getPropagation().getSetter();

        Property merged = new Property(parentProp.getPropertyName(), classElement);
        if (field != null) {
            merged.setField(field);
        }
        if (getter != null) {
            merged.setGetter(getter);
        }
        if (setter != null) {
            merged.setSetter(setter);
        }
        return merged;
    }

}
