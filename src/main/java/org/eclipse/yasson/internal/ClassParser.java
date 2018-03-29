/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 *     Maxence Laurent - parse default methods in interface as properties
 ******************************************************************************/
package org.eclipse.yasson.internal;

import org.eclipse.yasson.internal.model.*;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.model.customization.CreatorCustomization;

import javax.json.bind.JsonbException;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
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

        //reference property to creator parameter by name to merge configuration in runtime
        JsonbCreator creator = classModel.getClassCustomization().getCreator();
        if (creator != null) {
            sortedProperties.forEach((propertyModel -> {
                for (CreatorModel creatorModel : creator.getParams()) {
                    if (creatorModel.getName().equals(propertyModel.getPropertyName())) {
                        CreatorCustomization customization = (CreatorCustomization) creatorModel.getCustomization();
                        customization.setPropertyModel(propertyModel);
                    }
                }
            }));
        }
        classModel.setProperties(sortedProperties);

    }

    private void parseClassAndInterfaceMethods(JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        Class<?> concreteClass = classElement.getElement();
        parseMethods(concreteClass, classElement, classProperties);
        for (Class<?> ifc : jsonbContext.getAnnotationIntrospector().collectInterfaces(concreteClass)) {
            parseIfaceMethodAnnotations(ifc, classElement, classProperties);
        }
    }

    private void parseIfaceMethodAnnotations(Class<?> ifc, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        Method[] declaredMethods = AccessController.doPrivileged((PrivilegedAction<Method[]>) ifc::getDeclaredMethods);
        for (Method method : declaredMethods) {
            final String methodName = method.getName();
            if (!isPropertyMethod(method)) {
                continue;
            }
            String propertyName = toPropertyMethod(methodName);

            Property property = classProperties.get(propertyName);

            if (method.isDefault()) {
                // Interface provides default implementation
                if (property == null) {
                    // the property does not yet exists : create it from scratch
                    property = registerMethod(propertyName, method, classElement, classProperties);
                } else {
                    // property already exists, take care not overriding already parsed implementation
                    if (isSetter(method)) {
                        if (property.getSetter() == null) {
                            property.setSetter(method);
                        }
                    } else {
                        if (property.getGetter() == null) {
                            property.setGetter(method);
                        }
                    }
                }
            }

            if (property == null) {
                //May happen for classes which both extend a class with some method and implement interface with same method.
                continue;
            }
            JsonbAnnotatedElement<Method> methodElement = isGetter(method)
                    ? property.getGetterElement() : property.getSetterElement();
            //Only push iface annotations if not overridden on impl classes
            for (Annotation ann : method.getDeclaredAnnotations()) {
                if (methodElement.getAnnotation(ann.annotationType()) == null) {
                    methodElement.putAnnotation(ann);
                }
            }
        }
    }

    private Property registerMethod(String propertyName, Method method, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        Property property = classProperties.computeIfAbsent(propertyName, n -> new Property(n, classElement));
        if (isSetter(method)) {
            property.setSetter(method);
        } else {
            property.setGetter(method);
        }

        return property;
    }

    private void parseMethods(Class<?> clazz, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        Method[] declaredMethods = AccessController.doPrivileged((PrivilegedAction<Method[]>) clazz::getDeclaredMethods);
        for (Method method : declaredMethods) {
            String name = method.getName();
            if (!isPropertyMethod(method)) {
                continue;
            }
            final String propertyName = toPropertyMethod(name);

            Property property = registerMethod(propertyName, method, classElement, classProperties);
        }
    }

    private boolean isGetter(Method m) {
        return (m.getName().startsWith(GET_PREFIX) || m.getName().startsWith(IS_PREFIX)) && m.getParameterCount() == 0;
    }

    private boolean isSetter(Method m) {
        return m.getName().startsWith(SET_PREFIX) && m.getParameterCount() == 1;
    }

    private String toPropertyMethod(String name) {
        return Introspector.decapitalize(name.substring(name.startsWith(IS_PREFIX) ? 2 : 3, name.length()));
    }

    private boolean isPropertyMethod(Method m) {
        return isGetter(m) || isSetter(m);
    }

    private void parseFields(JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
        Field[] declaredFields = AccessController.doPrivileged(
                (PrivilegedAction<Field[]>) () -> classElement.getElement().getDeclaredFields());
        for (Field field : declaredFields) {
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

                if ((checkedPropertyModel.getReadName().equals(collectedPropertyModel.getReadName())
                && checkedPropertyModel.isReadable() && collectedPropertyModel.isReadable()) ||
                        (checkedPropertyModel.getWriteName().equals(collectedPropertyModel.getWriteName()))
                        && checkedPropertyModel.isWritable() && collectedPropertyModel.isWritable()) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_NAME_CLASH,
                            checkedPropertyModel.getPropertyName(), collectedPropertyModel.getPropertyName(),
                            cls.getName()));
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

    /**
     * Select the correct method to use. The correct method is the most specific
     * method which is not a default one:
     * <ul>
     * <li> if current is not defined, returns parent;</li>
     * <li> if parent is not defined, returns current;</li>
     * <li> if current is a default method and parent is not, returns parent;</li>
     * <ul>
     * <li><i>By definition, it is not possible to make a choice betweentwo default
     * methods. <br/>Here, the most specific is selected, but a concrete
     * implementation MUST eventually be provided as the source code won't even
     * compile if such a method does not exist</i></li>
     * </ul>
     * <li> returns current otherwise</li>
     * </ul>
     *
     *
     * @param current current 'child' implementation
     * @param parent  parent implementation
     *
     * @return effective method to register as getter or setter
     */
    private Method selectMostSpecificNonDefaultMethod(Method current, Method parent) {
        return (current != null ? (parent != null && current.isDefault()
                && !parent.isDefault() ? parent : current) : parent);
    }

    private Property mergeProperty(Property current, PropertyModel parentProp, JsonbAnnotatedElement<Class<?>> classElement) {
        Field field = current.getField() != null
                ? current.getField() : parentProp.getPropagation().getField();
        Method getter = selectMostSpecificNonDefaultMethod(current.getGetter(),
                parentProp.getPropagation().getGetter());
        Method setter = selectMostSpecificNonDefaultMethod(current.getSetter(),
                parentProp.getPropagation().getSetter());

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
