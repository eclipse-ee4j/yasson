/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.components.JsonbComponentInstanceCreatorFactory;
import org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Jsonb context holding central components and configuration of jsonb runtime. Scoped to instance of Jsonb runtime.
 *
 * @author Roman Grigoriadi
 */
public class JsonbContext {
    
    private static final Logger log = Logger.getLogger(JsonbContext.class.getName());

    private final JsonbConfig jsonbConfig;

    private final MappingContext mappingContext;

    private final JsonbComponentInstanceCreator componentInstanceCreator;

    private final JsonProvider jsonProvider;

    private final ComponentMatcher componentMatcher;

    private final AnnotationIntrospector annotationIntrospector;

    private final JsonbConfigProperties configProperties;

    private final InstanceCreator instanceCreator;

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
        this.instanceCreator = new InstanceCreator();
        this.componentInstanceCreator = initComponentInstanceCreator(instanceCreator);
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


    public JsonbConfigProperties getConfigProperties() {
        return configProperties;
    }


    /**
     * Returns component for creating instances of non-parsed types.
     * @return InstanceCreator
     */
    public InstanceCreator getInstanceCreator() {
        return instanceCreator;
    }

    private JsonbComponentInstanceCreator initComponentInstanceCreator(InstanceCreator instanceCreator) {
        ServiceLoader<JsonbComponentInstanceCreator> loader = AccessController
                .doPrivileged((PrivilegedAction<ServiceLoader<JsonbComponentInstanceCreator>>) () -> ServiceLoader
                        .load(JsonbComponentInstanceCreator.class));
        List<JsonbComponentInstanceCreator> creators = new ArrayList<>();
        for (JsonbComponentInstanceCreator creator : loader) {
            creators.add(creator);
        }
        if (creators.isEmpty()) {
            // No service provider found - use the defaults
            return JsonbComponentInstanceCreatorFactory.getComponentInstanceCreator(instanceCreator);
        }
        creators.sort(Comparator.comparingInt(JsonbComponentInstanceCreator::getPriority).reversed());
        JsonbComponentInstanceCreator creator = creators.get(0);
        log.finest("Component instance creator:" + creator.getClass());
        return creator;
    }

}
