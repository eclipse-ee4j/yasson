/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;

/**
 * JsonbBuilder implementation.
 */
public class JsonBindingBuilder implements JsonbBuilder {
    private JsonbConfig config = new JsonbConfig();
    private JsonProvider provider = null;
    private JsonBinding bindingCache = null;
    private Map<String, Object> configCache = null;
    private JsonProvider providerCache = null;

    @Override
    public JsonbBuilder withConfig(JsonbConfig config) {
        synchronized (this) {
            this.config = config;
            return this;
        }
    }

    @Override
    public JsonbBuilder withProvider(JsonProvider jsonpProvider) {
        synchronized (this) {
            this.provider = jsonpProvider;
            return this;
        }
    }

    /**
     * Gets configuration.
     *
     * @return configuration.
     */
    public JsonbConfig getConfig() {
        return config;
    }

    /**
     * Gets provider.
     *
     * @return Provider.
     */
    public Optional<JsonProvider> getProvider() {
        return Optional.ofNullable(provider);
    }

    @Override
    public Jsonb build() {
        synchronized (this) {
            if (bindingCache != null
                    && configEqualsCachedConfig()
                    && providerEqualsCachedProvider()) {
                return bindingCache;
            }
            JsonBinding jsonBinding = new JsonBinding(this);
            cacheCurrentConfiguration(jsonBinding);
            return jsonBinding;
        }
    }

    private boolean configEqualsCachedConfig() {
        return (configCache != null && config != null && configCache.equals(config.getAsMap()))
                || (config == null && configCache == null);
    }

    private boolean providerEqualsCachedProvider() {
        return (providerCache != null && providerCache.equals(provider))
                || (provider == null && providerCache == null);
    }

    private void cacheCurrentConfiguration(JsonBinding jsonBinding) {
        bindingCache = jsonBinding;
        if (config != null) {
            configCache = new HashMap<>(config.getAsMap());
        }
        providerCache = provider;
    }
}
