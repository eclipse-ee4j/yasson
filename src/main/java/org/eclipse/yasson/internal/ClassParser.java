/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyVisibilityStrategy;

import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbAnnotatedElement;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.Property;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Created a class internal model.
 */
class ClassParser {

    private static final String IS_PREFIX = "is";

    private static final String GET_PREFIX = "get";

    private static final String SET_PREFIX = "set";

    private final JsonbContext jsonbContext;

    ClassParser(JsonbContext jsonbContext) {
        this.jsonbContext = jsonbContext;
    }

    /**
     * Parse class fields and getters setters. Merge to java bean like properties.
     */
    void parseProperties(ClassModel classModel, JsonbAnnotatedElement<Class<?>> classElement) {
        final Map<String, Property> classProperties = new HashMap<>();
        parseFields(classElement, classProperties);
        parseClassAndInterfaceMethods(classElement, classProperties);

        //add sorted properties from parent, if they are not overridden in current class
        //parent properties are by default first by alphabet, than properties from a subclass
        final List<PropertyModel> sortedParentProperties = getSortedParentProperties(classModel, classElement, classProperties);

        List<PropertyModel> classPropertyModels = classProperties.values().stream()
                .map(property -> new PropertyModel(classModel, property, jsonbContext))
                .collect(Collectors.toList());

        //check for collision on same property read name
        List<PropertyModel> unsortedMerged = new ArrayList<>(sortedParentProperties.size() + classPropertyModels.size());
        unsortedMerged.addAll(sortedParentProperties);
        unsortedMerged.addAll(classPropertyModels);
        checkPropertyNameClash(unsortedMerged, classModel.getType());

        mergePropertyModels(classPropertyModels);

        List<PropertyModel> sortedPropertyModels = new ArrayList<>(sortedParentProperties.size() + classPropertyModels.size());
        sortedPropertyModels.addAll(sortedParentProperties);
        sortedPropertyModels.addAll(jsonbContext.getConfigProperties().getPropertyOrdering()
                                            .orderProperties(classPropertyModels, classModel));

        //reference property to creator parameter by name to merge configuration in runtime
        JsonbCreator creator = classModel.getClassCustomization().getCreator();
        if (creator != null) {
            sortedPropertyModels.forEach(propertyModel -> {
                for (CreatorModel creatorModel : creator.getParams()) {
                    if (creatorModel.getName().equals(propertyModel.getPropertyName())) {
                        creatorModel.getCustomization().setPropertyModel(propertyModel);
                    }
                }
            });
        }

        classModel.setProperties(sortedPropertyModels);

    }

    private static void mergePropertyModels(List<PropertyModel> unsortedMerged) {
        PropertyModel[] clone = unsortedMerged.toArray(new PropertyModel[0]);
        for (int i = 0; i < clone.length; i++) {
            for (int j = i + 1; j < clone.length; j++) {
                PropertyModel firstPropertyModel = clone[i];
                PropertyModel secondPropertyModel = clone[j];
                if (firstPropertyModel.equals(secondPropertyModel)) {
                    // Need to merge two properties
                    unsortedMerged.remove(firstPropertyModel);
                    unsortedMerged.remove(secondPropertyModel);
                    if (!firstPropertyModel.isReadable() && !firstPropertyModel.isWritable()) {
                        unsortedMerged.add(secondPropertyModel);
                    } else if (!secondPropertyModel.isReadable() && !secondPropertyModel.isWritable()) {
                        unsortedMerged.add(firstPropertyModel);
                    } else {
                        unsortedMerged.add(new PropertyModel(firstPropertyModel, secondPropertyModel));
                    }
                }
            }
        }
    }

    private void parseClassAndInterfaceMethods(JsonbAnnotatedElement<Class<?>> classElement,
                                               Map<String, Property> classProperties) {
        Class<?> concreteClass = classElement.getElement();
        parseMethods(concreteClass, classElement, classProperties);
        for (Class<?> ifc : jsonbContext.getAnnotationIntrospector().collectInterfaces(concreteClass)) {
            parseIfaceMethodAnnotations(ifc, classElement, classProperties);
        }
    }

    private void parseIfaceMethodAnnotations(Class<?> ifc,
                                             JsonbAnnotatedElement<Class<?>> classElement,
                                             Map<String, Property> classProperties) {
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
                if (methodElement.getAnnotation(ann.annotationType()).isEmpty()) {
                    methodElement.putAnnotation(ann, true, null);
                }
            }
        }
    }

    private Property registerMethod(String propertyName,
                                    Method method,
                                    JsonbAnnotatedElement<Class<?>> classElement,
                                    Map<String, Property> classProperties) {
        Property property = classProperties.computeIfAbsent(propertyName, n -> new Property(n, classElement));
        if (isSetter(method)) {
            property.setSetter(method);
        } else {
            property.setGetter(method);
        }

        return property;
    }

    private void parseMethods(Class<?> clazz,
                              JsonbAnnotatedElement<Class<?>> classElement,
                              Map<String, Property> classProperties) {
        Method[] declaredMethods = AccessController.doPrivileged((PrivilegedAction<Method[]>) clazz::getDeclaredMethods);
        for (Method method : declaredMethods) {
            String name = method.getName();
            //isBridge method filters out methods inherited from interfaces
            boolean isAccessorMethod = ClassMultiReleaseExtension.isSpecialAccessorMethod(method, classProperties)
                    || isPropertyMethod(method);
            if (!isAccessorMethod || method.isBridge() || isSpecialCaseMethod(clazz, method)) {
                continue;
            }
            final String propertyName = ClassMultiReleaseExtension.shouldTransformToPropertyName(method)
                    ? toPropertyMethod(name)
                    : name;

            registerMethod(propertyName, method, classElement, classProperties);
        }
    }

    /**
     * Filter out certain methods that get forcibly added to some classes.
     * For example the public groovy.lang.MetaClass X.getMetaClass() method from Groovy classes
     */
    private static boolean isSpecialCaseMethod(Class<?> clazz, Method m) {
        if (!Modifier.isPublic(m.getModifiers()) || Modifier.isStatic(m.getModifiers()) || m.isSynthetic()) {
            return false;
        }
        // Groovy objects will have public groovy.lang.MetaClass X.getMetaClass()
        // which causes an infinite loop in serialization
        if (m.getName().equals("getMetaClass")
                && m.getReturnType().getCanonicalName().equals("groovy.lang.MetaClass")) {
            return true;
        }
        // WELD proxy objects will have 'public org.jboss.weld
        if (m.getName().equals("getMetadata")
                && m.getReturnType().getCanonicalName().equals("org.jboss.weld.proxy.WeldClientProxy$Metadata")) {
            return true;
        }
        return false;
    }

    private static boolean isGetter(Method m) {
        return (m.getName().startsWith(GET_PREFIX) || m.getName().startsWith(IS_PREFIX)) && m.getParameterCount() == 0;
    }

    private static boolean isSetter(Method m) {
        return m.getName().startsWith(SET_PREFIX) && m.getParameterCount() == 1;
    }

    private static String toPropertyMethod(String name) {
        return lowerFirstLetter(name.substring(name.startsWith(IS_PREFIX) ? 2 : 3));
    }

    private static String lowerFirstLetter(String name) {
        Objects.requireNonNull(name);
        if (name.length() == 0) {
            //methods named get() or set()
            return name;
        }
        if (name.length() > 1
                && Character.isUpperCase(name.charAt(1))
                && Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static boolean isPropertyMethod(Method m) {
        return isGetter(m) || isSetter(m);
    }

    private static void parseFields(JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
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

    private static void checkPropertyNameClash(List<PropertyModel> collectedProperties, Class<?> cls) {
        final List<PropertyModel> checkedProperties = new ArrayList<>();
        for (PropertyModel collectedPropertyModel : collectedProperties) {
            for (PropertyModel checkedPropertyModel : checkedProperties) {
                if ((checkedPropertyModel.getReadName().equals(collectedPropertyModel.getReadName())
                        && checkedPropertyModel.isReadable() //
                        && collectedPropertyModel.isReadable())
                        || (checkedPropertyModel.getWriteName().equals(collectedPropertyModel.getWriteName())
                                && checkedPropertyModel.isWritable() //
                                && collectedPropertyModel.isWritable())) {
                    throw new JsonbException(
                            Messages.getMessage(MessageKeys.PROPERTY_NAME_CLASH, checkedPropertyModel.getPropertyName(),
                                    collectedPropertyModel.getPropertyName(), cls.getName()));
                }
            }
            checkedProperties.add(collectedPropertyModel);
        }
    }

    /**
     * Merges current class properties with parent class properties.
     * If javabean property is declared in more than one inheritance levels,
     * merge field, getters and setters of that property.
     * <p>
     * For example BaseClass contains field foo and getter getFoo. In BaseExtensions there is a setter setFoo.
     * All three will be merged for BaseExtension.
     * <p>
     * Such property is sorted based on where its getter or field is located.
     */
    private List<PropertyModel> getSortedParentProperties(ClassModel classModel,
                                                          JsonbAnnotatedElement<Class<?>> classElement,
                                                          Map<String, Property> classProperties) {
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
                    PropertyVisibilityStrategy propertyVisibilityStrategy = classModel.getClassCustomization()
                            .getPropertyVisibilityStrategy();

                    if (PropertyModel.isPropertyReadable(current.getField(), current.getGetter(), propertyVisibilityStrategy)) {
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
     * @param current current 'child' implementation
     * @param parent  parent implementation
     * @return effective method to register as getter or setter
     */
    private static Method selectMostSpecificNonDefaultMethod(Method current, Method parent) {
        return (
                current != null ? (
                        parent != null && current.isDefault()
                                && !parent.isDefault() ? parent : current) : parent);
    }

    private static Property mergeProperty(Property current,
                                          PropertyModel parentProp,
                                          JsonbAnnotatedElement<Class<?>> classElement) {
        Field field = current.getField() != null
                ? current.getField() : parentProp.getField();
        Method getter = selectMostSpecificNonDefaultMethod(current.getGetter(),
                                                           parentProp.getGetter());
        Method setter = selectMostSpecificNonDefaultMethod(current.getSetter(),
                                                           parentProp.getSetter());

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
