/*
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;

import org.eclipse.yasson.internal.components.AbstractComponentBinding;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.ComponentBindings;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.customization.ComponentBoundCustomization;

/**
 * Searches for a registered components or Serializer for a given type.
 */
public class ComponentMatcher {

    private final JsonbContext jsonbContext;

    /**
     * Flag for searching for generic serializers and adapters in runtime.
     */
    private volatile boolean genericComponents;

    private final ConcurrentMap<Type, ComponentBindings<?, ?>> userComponents;

    /**
     * Create component matcher.
     *
     * @param context mandatory
     */
    ComponentMatcher(JsonbContext context) {
        Objects.requireNonNull(context);
        this.jsonbContext = context;
        userComponents = new ConcurrentHashMap<>();
        init();
    }

    /**
     * Called during context creation, introspecting user components provided with JsonbConfig.
     */
    void init() {
        final JsonbSerializer<?>[] serializers = (JsonbSerializer<?>[]) jsonbContext.getConfig()
                .getProperty(JsonbConfig.SERIALIZERS).orElseGet(() -> new JsonbSerializer<?>[] {});
        for (JsonbSerializer<?> serializer : serializers) {
            @SuppressWarnings("unchecked")
            SerializerBinding<?> serializerBinding = introspectSerializerBinding(serializer.getClass(), serializer);
            addSerializer(serializerBinding.getBindingType(), serializerBinding);
        }
        final JsonbDeserializer<?>[] deserializers = (JsonbDeserializer<?>[]) jsonbContext.getConfig()
                .getProperty(JsonbConfig.DESERIALIZERS).orElseGet(() -> new JsonbDeserializer<?>[] {});
        for (JsonbDeserializer<?> deserializer : deserializers) {
            @SuppressWarnings("unchecked")
            DeserializerBinding<?> deserializerBinding = introspectDeserializerBinding(deserializer.getClass(), deserializer);
            addDeserializer(deserializerBinding.getBindingType(), deserializerBinding);
        }

        final JsonbAdapter<?, ?>[] adapters = (JsonbAdapter<?, ?>[]) jsonbContext.getConfig().getProperty(JsonbConfig.ADAPTERS)
                .orElseGet(() -> new JsonbAdapter<?, ?>[] {});
        for (JsonbAdapter<?, ?> adapter : adapters) {
            @SuppressWarnings("unchecked")
            AdapterBinding<?, ?> adapterBinding = introspectAdapterBinding(adapter.getClass(), adapter);
            addAdapter(adapterBinding.getBindingType(), adapterBinding);
        }
    }

    private ComponentBindings<?, ?> getBindingInfo(Type type) {
        return userComponents
                .compute(type, (type1, bindingInfo) -> bindingInfo != null ? bindingInfo : new ComponentBindings<>(type1));
    }

    private <T> void addSerializer(Type bindingType, SerializerBinding<T> serializer) {
        userComponents.computeIfPresent(bindingType, (type, bindings) -> {
            if (bindings.getSerializerBinding() != null) {
                return bindings;
            }
            registerGeneric(bindingType);
            @SuppressWarnings({"unchecked", "rawtypes"})
            ComponentBindings componentBindings = new ComponentBindings(bindings, serializer);
            return componentBindings;
        });
    }

    private <T> void addDeserializer(Type bindingType, DeserializerBinding<T> deserializer) {
        userComponents.computeIfPresent(bindingType, (type, bindings) -> {
            if (bindings.getDeserializerBinding() != null) {
                return bindings;
            }
            registerGeneric(bindingType);
            @SuppressWarnings({"unchecked", "rawtypes"})
            ComponentBindings componentBindings = new ComponentBindings(bindings, deserializer);
            return componentBindings;
        });
    }

    private <Original, Adapted> void addAdapter(Type bindingType, AdapterBinding<Original, Adapted> adapter) {
        userComponents.computeIfPresent(bindingType, (type, bindings) -> {
            if (bindings.getAdapterBinding() != null) {
                return bindings;
            }
            registerGeneric(bindingType);
            @SuppressWarnings({"unchecked", "rawtypes"})
            ComponentBindings componentBindings = new ComponentBindings(bindings, adapter);
            return componentBindings;
        });
    }

    /**
     * If type is not parametrized runtime component resolution doesn't has to happen.
     *
     * @param bindingType component binding type
     */
    private void registerGeneric(Type bindingType) {
        if (bindingType instanceof ParameterizedType && !genericComponents) {
            genericComponents = true;
        }
    }

    /**
     * Lookup serializer binding for a given property runtime type.
     *
     * @param propertyRuntimeType runtime type of a property
     * @param customization       with component info
     * @return serializer optional
     */
    public Optional<SerializerBinding<?>> getSerializerBinding(Type propertyRuntimeType,
                                                               ComponentBoundCustomization customization) {

        if (customization == null || customization.getSerializerBinding() == null) {
            return searchComponentBinding(propertyRuntimeType, ComponentBindings::getSerializerBinding);
        }
        return Optional.of(customization.getSerializerBinding());
    }

    /**
     * Lookup deserializer binding for a given property runtime type.
     *
     * @param propertyRuntimeType runtime type of a property
     * @param customization       customization with component info
     * @return serializer optional
     */
    public Optional<DeserializerBinding<?>> getDeserializerBinding(Type propertyRuntimeType,
                                                                   ComponentBoundCustomization customization) {
        if (customization == null || customization.getDeserializerBinding() == null) {
            return searchComponentBinding(propertyRuntimeType, ComponentBindings::getDeserializerBinding);
        }
        return Optional.of(customization.getDeserializerBinding());
    }

    /**
     * Get components from property model (if declared by annotation and runtime type matches),
     * or return components searched by runtime type.
     *
     * @param propertyRuntimeType runtime type not null
     * @param customization       customization with component info
     * @return components info if present
     */
    public Optional<AdapterBinding<?, ?>> getSerializeAdapterBinding(Type propertyRuntimeType,
                                                               ComponentBoundCustomization customization) {
        if (customization == null || customization.getSerializeAdapterBinding() == null) {
            return searchComponentBinding(propertyRuntimeType, ComponentBindings::getAdapterBinding);
        }
        return Optional.of(customization.getSerializeAdapterBinding());
    }

    /**
     * Get components from property model (if declared by annotation and runtime type matches),
     * or return components searched by runtime type.
     *
     * @param propertyRuntimeType runtime type not null
     * @param customization       customization with component info
     * @return components info if present
     */
    public Optional<AdapterBinding<?, ?>> getDeserializeAdapterBinding(Type propertyRuntimeType,
                                                                 ComponentBoundCustomization customization) {
        if (customization == null || customization.getDeserializeAdapterBinding() == null) {
            return searchComponentBinding(propertyRuntimeType, ComponentBindings::getAdapterBinding);
        }
        return Optional.of(customization.getDeserializeAdapterBinding());
    }

    private <C, T extends AbstractComponentBinding<? extends C>> Optional<T> searchComponentBinding(Type runtimeType, Function<ComponentBindings<?, ?>, T> supplier) {
        // First check if there is an exact match
        Optional<T> match = getMatchingBinding(runtimeType, supplier);
        if (match.isPresent()) {
            return match;
        }

        Optional<Class<?>> runtimeClass = ReflectionUtils.getOptionalRawType(runtimeType);
        if (runtimeClass.isPresent()) {
            // Check if any interfaces have a match
            for (Class<?> ifc : runtimeClass.get().getInterfaces()) {
                match = getMatchingBinding(ifc, supplier);
                if (match.isPresent()) {
                    return match;
                }
            }
            
            // check if the superclass has a match
            Class<?> superClass = runtimeClass.get().getSuperclass();
            if (superClass != null && superClass != Object.class) {
                Optional<T> superBinding = searchComponentBinding(superClass, supplier);
                if (superBinding.isPresent()) {
                    return superBinding;
                }
            }
        }
        
        return Optional.empty();
    }

    private <C, T extends AbstractComponentBinding<? extends  C>> Optional<T> getMatchingBinding(Type runtimeType, Function<ComponentBindings<?, ?>, T> supplier) {
        ComponentBindings<?, ?> binding = userComponents.get(runtimeType);
        if (binding != null) {
            Optional<T> match = getMatchingBinding(runtimeType, binding, supplier);
            if (match.isPresent()) {
                return match;
            }
        }
        return Optional.empty();
    }

    private <T> Optional<T> getMatchingBinding(Type runtimeType, ComponentBindings<?, ?> binding, Function<ComponentBindings<?, ?>, T> supplier) {
        if (matches(runtimeType, binding.getBindingType())) {
            final T component = supplier.apply(binding);
            if (component != null) {
                return Optional.of(component);
            }
        }
        return Optional.empty();
    }

    private boolean matches(Type runtimeType, Type componentBindingType) {
        if (componentBindingType.equals(runtimeType)) {
            return true;
        }

        if (componentBindingType instanceof Class && runtimeType instanceof Class) {
            return ((Class<?>) componentBindingType).isAssignableFrom((Class<?>) runtimeType);
        }

        //don't try to runtime generic scan if not needed
        if (!genericComponents) {
            return false;
        }

        return runtimeType instanceof ParameterizedType && componentBindingType instanceof ParameterizedType
                && ReflectionUtils.getRawType(componentBindingType).isAssignableFrom(ReflectionUtils.getRawType(runtimeType))
                && matchTypeArguments((ParameterizedType) runtimeType, (ParameterizedType) componentBindingType);
    }

    /**
     * If runtimeType to adapt is a ParametrizedType, check all type args to match against components args.
     */
    private boolean matchTypeArguments(ParameterizedType requiredType, ParameterizedType componentBound) {
        final Type[] requiredTypeArguments = requiredType.getActualTypeArguments();
        final Type[] adapterBoundTypeArguments = componentBound.getActualTypeArguments();
        if (requiredTypeArguments.length != adapterBoundTypeArguments.length) {
            return false;
        }
        for (int i = 0; i < requiredTypeArguments.length; i++) {
            Type adapterTypeArgument = adapterBoundTypeArguments[i];
            if (!requiredTypeArguments[i].equals(adapterTypeArgument)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Introspect components generic information and put resolved types into metadata wrapper.
     *
     * @param adapterClass class of an components
     * @param instance     components instance
     * @return introspected info with resolved typevar types.
     */
    <Original, Adapted, A extends JsonbAdapter<Original, Adapted>> AdapterBinding<?, ?> introspectAdapterBinding(Class<? extends A> adapterClass, A instance) {
        final ParameterizedType adapterRuntimeType = ReflectionUtils.findParameterizedType(adapterClass, JsonbAdapter.class);
        final Type[] adapterTypeArguments = adapterRuntimeType.getActualTypeArguments();
        Type adaptFromType = resolveTypeArg(adapterTypeArguments[0], adapterClass);
        Type adaptToType = resolveTypeArg(adapterTypeArguments[1], adapterClass);
        final ComponentBindings<?, ?> componentBindings = getBindingInfo(adaptFromType);
        if (componentBindings.getAdapterBinding() != null && componentBindings.getAdapterBinding().getComponentClass()
                .equals(adapterClass)) {
            return componentBindings.getAdapterBinding();
        }
        A newAdapter = instance != null
                ? instance
                : jsonbContext.getComponentInstanceCreator().getOrCreateComponent(adapterClass);
        return new AdapterBinding<>(adaptFromType, adaptToType, newAdapter);
    }

    /**
     * If an instance of deserializerClass is present in context and is bound for same type, return that instance.
     * Otherwise create new instance and set it to context.
     *
     * @param deserializerClass class of deserializer
     * @param instance          instance to use if not cached already
     * @return wrapper used in property models
     */
    <T, D extends JsonbDeserializer<T>> DeserializerBinding<?> introspectDeserializerBinding(Class<? extends D> deserializerClass, D instance) {
        final ParameterizedType deserializerRuntimeType = ReflectionUtils
                .findParameterizedType(deserializerClass, JsonbDeserializer.class);
        Type deserializerBindingType = resolveTypeArg(deserializerRuntimeType.getActualTypeArguments()[0], deserializerClass);
        final ComponentBindings<?, ?> componentBindings = getBindingInfo(deserializerBindingType);
        if (componentBindings.getDeserializerBinding() != null && componentBindings.getDeserializerBinding().getComponentClass()
                .equals(deserializerClass)) {
            return componentBindings.getDeserializerBinding();
        } else {
            D deserializer = instance != null ? instance : jsonbContext.getComponentInstanceCreator()
                    .getOrCreateComponent(deserializerClass);
            return new DeserializerBinding<>(deserializerBindingType, deserializer);
        }
    }

    /**
     * If an instance of serializerClass is present in context and is bound for same type, return that instance.
     * Otherwise create new instance and set it to context.
     *
     * @param serializerClass class of deserializer
     * @param instance        instance to use if not cached
     * @return wrapper used in property models
     */

    <T, S extends JsonbSerializer<T>> SerializerBinding<?> introspectSerializerBinding(Class<? extends S> serializerClass, S instance) {
        final ParameterizedType serializerRuntimeType = ReflectionUtils
                .findParameterizedType(serializerClass, JsonbSerializer.class);
        Type serBindingType = resolveTypeArg(serializerRuntimeType.getActualTypeArguments()[0], serializerClass);
        final ComponentBindings<?, ?> componentBindings = getBindingInfo(serBindingType);
        if (componentBindings.getSerializerBinding() != null && componentBindings.getSerializerBinding().getComponentClass().equals(serializerClass)) {
            return componentBindings.getSerializerBinding();
        } else {
            S serializer = instance != null ? instance : jsonbContext.getComponentInstanceCreator()
                    .getOrCreateComponent(serializerClass);
            return new SerializerBinding<>(serBindingType, serializer);
        }

    }

    private Type resolveTypeArg(Type adapterTypeArg, Type adapterType) {
        if (adapterTypeArg instanceof ParameterizedType) {
            return ReflectionUtils.resolveTypeArguments((ParameterizedType) adapterTypeArg, adapterType);
        } else if (adapterTypeArg instanceof TypeVariable) {
            LinkedList<Type> chain = new LinkedList<>();
            chain.add(adapterType);
            return ReflectionUtils.resolveItemVariableType(chain, (TypeVariable<?>) adapterTypeArg, true);
        } else {
            return adapterTypeArg;
        }
    }

}
