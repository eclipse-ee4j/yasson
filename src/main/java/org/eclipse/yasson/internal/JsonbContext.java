/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal;

import org.eclipse.yasson.internal.components.InstanceCreatorFactoryHelper;
import org.eclipse.yasson.internal.components.JsonbComponentInstanceCreator;

import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Jsonb context holding central components and configuration of jsonb runtime. Scoped to instance of Jsonb runtime.
 * Thread bound.
 *
 * @author Roman Grigoriadi
 */
public class JsonbContext {

    private final JsonbConfig jsonbConfig;

    private final MappingContext mappingContext;

    private final JsonbComponentInstanceCreator componentInstanceCreator;

    private final JsonProvider jsonProvider;

    private final ComponentMatcher componentMatcher;

    private final AnnotationIntrospector annotationIntrospector;

    private boolean genericComponents;

    private JsonbConfigProperties configProperties;

    /**
     * Types which are being processed by {@linkplain javax.json.bind.serializer.JsonbSerializer},
     * {@link javax.json.bind.serializer.JsonbDeserializer} or
     * {@link javax.json.bind.adapter.JsonbAdapter}.
     *
     * Used to avoid StackOverflowError, when adapted / serialized object
     * contains contains instance of its type inside it.
     */
    private Set<Type> bindingTypes;

    /**
     * Creates and initialize context.
     *
     * @param jsonbConfig jsonb jsonbConfig not null
     * @param jsonProvider provider of JSONP
     */
    public JsonbContext(JsonbConfig jsonbConfig, JsonProvider jsonProvider) {
        Objects.requireNonNull(jsonbConfig);
        this.jsonbConfig = jsonbConfig;
        this.mappingContext = new MappingContext(this);
        this.componentInstanceCreator = InstanceCreatorFactoryHelper.getComponentInstanceCreator();
        this.componentMatcher = new ComponentMatcher(this);
        this.annotationIntrospector = new AnnotationIntrospector(this);
        this.jsonProvider = jsonProvider;
        this.configProperties = new JsonbConfigProperties(jsonbConfig);
        this.bindingTypes = new HashSet<>();
    }

    /**
     * Gets {@link JsonbConfig}.
     *
     * @return Configuration.
     */
    public JsonbConfig getConfig() {
        return jsonbConfig;
    }

    /**
     * Gets mapping context.
     *
     * @return Mapping context.
     */
    public MappingContext getMappingContext() {
        return mappingContext;
    }


    /**
     * Gets JSONP provider.
     *
     * @return JSONP provider.
     */
    public JsonProvider getJsonProvider() {
        return jsonProvider;
    }

    /**
     * Implementation creating instances of user components used by JSONB, such as adapters and strategies.
     *
     * @return Instance creator.
     */
    public JsonbComponentInstanceCreator getComponentInstanceCreator() {
        return componentInstanceCreator;
    }

    /**
     * Component matcher for lookup of (de)serializers and adapters.
     *
     * @return Component matcher.
     */
    public ComponentMatcher getComponentMatcher() {
        return componentMatcher;
    }

    /**
     * Gets component for annotation parsing.
     *
     * @return Annotation introspector.
     */
    public AnnotationIntrospector getAnnotationIntrospector() {
        return annotationIntrospector;
    }


    /**
     * Flag for searching for generic serializers and adapters in runtime.
     *
     * @return True if generic components are present.
     */
    public boolean genericComponentsPresent() {
        return genericComponents;
    }

    /**
     * Set flag for searching for generic serializers and adapters in runtime.
     */
    public void registerGenericComponentFlag() {
        this.genericComponents = true;
    }

    public JsonbConfigProperties getConfigProperties() {
        return configProperties;
    }

    public boolean addProcessedType(Type bindingTypes) {
        return this.bindingTypes.add(bindingTypes);
    }

    public boolean removeProcessedType(Type bindingType) {
        return bindingTypes.remove(bindingType);
    }

    /**
     * Check if type is already being processed lower in call stack.
     *
     * This may happen when {@link javax.json.bind.adapter.JsonbAdapter}
     * or {@link javax.json.bind.serializer.JsonbSerializer} are called recursively for same binding type.
     *
     * @param bindingType type to check
     * @return true if type is processed
     */
    public boolean containsProcessedType(Type bindingType) {
        return bindingTypes.contains(bindingType);
    }
}
