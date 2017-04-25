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

import org.eclipse.yasson.internal.cdi.JsonbComponentInstanceCreator;
import org.eclipse.yasson.internal.cdi.JsonbComponentInstanceCreatorFactory;

import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;
import java.util.Objects;

/**
 * Jsonb context holding central components and configuration of jsonb runtime. Scoped to instance of Jsonb runtime.
 * Thread safe.
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
     * Creates and initialize context.
     *
     * @param jsonbConfig jsonb jsonbConfig not null
     * @param jsonProvider provider of JSONP
     */
    public JsonbContext(JsonbConfig jsonbConfig, JsonProvider jsonProvider) {
        Objects.requireNonNull(jsonbConfig);
        this.jsonbConfig = jsonbConfig;
        this.mappingContext = new MappingContext(this);
        this.componentInstanceCreator = JsonbComponentInstanceCreatorFactory.getComponentInstanceCreator();
        this.componentMatcher = new ComponentMatcher(this);
        this.annotationIntrospector = new AnnotationIntrospector(this);
        this.jsonProvider = jsonProvider;
        this.configProperties = new JsonbConfigProperties(jsonbConfig);
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
}
