/*
 * Copyright (c) 2016, 2024 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

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

    private interface ComponentBindingsFunction<C, B extends AbstractComponentBinding<? extends C>>
            extends BiFunction<ComponentBindings<?, ?>, B, ComponentBindings<?, ?>> {}

    /**
     * Called during context creation, introspecting user components provided with JsonbConfig.
     */
    void init() {
        //Process serializers
        final JsonbSerializer<?>[] serializers = (JsonbSerializer<?>[]) jsonbContext.getConfig()
                .getProperty(JsonbConfig.SERIALIZERS).orElseGet(() -> new JsonbSerializer<?>[] {});
        @SuppressWarnings("unchecked")
        Function<JsonbSerializer<?>, SerializerBinding<?>> introspectSerializerFunction = (serializer) ->
                introspectSerializerBinding(serializer.getClass(), serializer);
        @SuppressWarnings({"unchecked", "rawtypes"})
        ComponentBindingsFunction<JsonbSerializer<?>, SerializerBinding<?>> createSerializerBindingFunction = (bindings, newBinding) ->
                new ComponentBindings(bindings, newBinding);
        addToComponentBindings(serializers, introspectSerializerFunction, ComponentBindings::getSerializerBinding, createSerializerBindingFunction);

        //Process deserializers
        final JsonbDeserializer<?>[] deserializers = (JsonbDeserializer<?>[]) jsonbContext.getConfig()
                .getProperty(JsonbConfig.DESERIALIZERS).orElseGet(() -> new JsonbDeserializer<?>[] {});
        @SuppressWarnings("unchecked")
        Function<JsonbDeserializer<?>, DeserializerBinding<?>>  introspectDeserializerFunction = (deserializer) ->
                introspectDeserializerBinding(deserializer.getClass(), deserializer);
        @SuppressWarnings({"unchecked", "rawtypes"})
        ComponentBindingsFunction<JsonbDeserializer<?>, DeserializerBinding<?>> createDeserializerBindingFunction = (bindings, newBinding) ->
                new ComponentBindings(bindings, newBinding);
        addToComponentBindings(deserializers, introspectDeserializerFunction, ComponentBindings::getDeserializerBinding, createDeserializerBindingFunction);

        //Process adapters
        final JsonbAdapter<?, ?>[] adapters = (JsonbAdapter<?, ?>[]) jsonbContext.getConfig().getProperty(JsonbConfig.ADAPTERS)
                .orElseGet(() -> new JsonbAdapter<?, ?>[] {});
        @SuppressWarnings("unchecked")
        Function<JsonbAdapter<?, ?>, AdapterBinding<?, ?>>  introspectAdapterFunction = (adapter) -> introspectAdapterBinding(adapter.getClass(), adapter);
        @SuppressWarnings({"unchecked", "rawtypes"})
        ComponentBindingsFunction<JsonbAdapter<?, ?>, AdapterBinding<?, ?>> createAdapterBindingFunction = (bindings, newBinding) ->
                new ComponentBindings(bindings, newBinding);
        addToComponentBindings(adapters, introspectAdapterFunction, ComponentBindings::getAdapterBinding, createAdapterBindingFunction);
    }

    private ComponentBindings<?, ?> getBindingInfo(Type type) {
        return userComponents
                .compute(type, (type1, bindingInfo) -> bindingInfo != null ? bindingInfo : new ComponentBindings<>(type1));
    }

    private <C, B extends AbstractComponentBinding<? extends C>> void addToComponentBindings(C[] customisations, Function<C, B> introspectFunction,
            Function<ComponentBindings<?, ?>, B> getExistingBinding,
            ComponentBindingsFunction<C, B> createNewComponentBindings) {

        Objects.requireNonNull(customisations);
        Objects.requireNonNull(introspectFunction);
        Objects.requireNonNull(getExistingBinding);
        Objects.requireNonNull(createNewComponentBindings);

        for (C customisation : customisations) {
            B componentBinding = introspectFunction.apply(customisation);
            Type bindingType = componentBinding.getBindingType();

            userComponents.computeIfPresent(bindingType, (type, bindings) -> {
                if (getExistingBinding.apply(bindings) != null) {
                    return bindings;
                }
                registerGeneric(bindingType);
                return createNewComponentBindings.apply(bindings, componentBinding);
            });
        }
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

    private <C, T extends AbstractComponentBinding<? extends C>> Optional<T> searchComponentBinding(Type runtimeType,
            Function<ComponentBindings<?, ?>, T> bindingGetter) {
        // First check if there is an exact match
        Optional<T> match = getMatchingBinding(runtimeType, bindingGetter);
        if (match.isPresent()) {
            return match;
        }

        Optional<Class<?>> runtimeClass = ReflectionUtils.getOptionalRawType(runtimeType);
        return runtimeClass.map(clazz -> {
            // Check if any interfaces have a match
            Optional<T> interfaceMatch = findBindingInClasses(Arrays.stream(clazz.getInterfaces()), ifc -> getMatchingBinding(ifc, bindingGetter));
            if (interfaceMatch.isPresent()) {
                return interfaceMatch;
            }

            // check if the superclass has a match
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                Optional<T> superBinding =
                        findBindingInClasses(Stream.of(superClass), superClazz -> searchComponentBinding(superClazz, bindingGetter));
                if (superBinding.isPresent()) {
                    return superBinding;
                }
            }
            return Optional.<T>empty();
        }).orElse(Optional.empty());
    }

    private <T> Optional<T> findBindingInClasses(Stream<Class<?>> stream, Function<Class<?>, Optional<T>> mapper) {
        return stream
                .map(mapper)
                .filter(Optional::isPresent)
                .findFirst().orElse(Optional.empty());
    }

    private <C, T extends AbstractComponentBinding<? extends C>> Optional<T> getMatchingBinding(Type runtimeType,
            Function<ComponentBindings<?, ?>, T> bindingGetter) {
        ComponentBindings<?, ?> binding = userComponents.get(runtimeType);
        if (binding != null) {
            return (matches(runtimeType, binding.getBindingType())) ? Optional.ofNullable(bindingGetter.apply(binding)) : Optional.empty();
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

    private <T, C, B extends AbstractComponentBinding<? extends C>> B introspectBinding(Class<? extends T> customisationClass, T customisationInstance,
            Class<?> customisationClassToFind, Function<ComponentBindings<?, ?>, B> getExistingBinding,
            BiFunction<Type[], T, B> createNewBinding) {

        Objects.requireNonNull(customisationClass);
        Objects.requireNonNull(customisationClassToFind);
        Objects.requireNonNull(getExistingBinding);
        Objects.requireNonNull(createNewBinding);
        final ParameterizedType customisationRuntimeType = ReflectionUtils.findParameterizedType(customisationClass, customisationClassToFind);
        Type customisationBindingType = resolveTypeArg(customisationRuntimeType.getActualTypeArguments()[0], customisationClass);
        final ComponentBindings<?, ?> componentBindings = getBindingInfo(customisationBindingType);
        B binding = getExistingBinding.apply(componentBindings);
        if (binding != null && customisationClass.equals(binding.getComponentClass())) {
            return binding;
        } else {
            T customisation = customisationInstance != null ? customisationInstance : jsonbContext.getComponentInstanceCreator()
                    .getOrCreateComponent(customisationClass);
            return createNewBinding.apply(customisationRuntimeType.getActualTypeArguments(), customisation);
        }
    }

    /**
     * Introspect components generic information and put resolved types into metadata wrapper.
     *
     * @param adapterClass class of an components
     * @param instance     components instance
     * @return introspected info with resolved typevar types.
     */
    <Original, Adapted, A extends JsonbAdapter<Original, Adapted>> AdapterBinding<?, ?> introspectAdapterBinding(Class<? extends A> adapterClass, A instance) {
        return introspectBinding(adapterClass, instance, JsonbAdapter.class, ComponentBindings::getAdapterBinding,
                (typeArgs, adapter) -> new AdapterBinding<>(resolveTypeArg(typeArgs[0], adapterClass), resolveTypeArg(typeArgs[1], adapterClass), adapter));
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
        return introspectBinding(deserializerClass, instance, JsonbDeserializer.class, ComponentBindings::getDeserializerBinding,
                (typeArgs, deserializer) -> new DeserializerBinding<>(resolveTypeArg(typeArgs[0], deserializerClass), deserializer));
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
        return introspectBinding(serializerClass, instance, JsonbSerializer.class, ComponentBindings::getSerializerBinding,
                (typeArgs, serializer) -> new SerializerBinding<>(resolveTypeArg(typeArgs[0], serializerClass), serializer));
    }

    private Type resolveTypeArg(Type adapterTypeArg, Type adapterType) {
        if (adapterTypeArg instanceof ParameterizedType) {
            return ReflectionUtils.resolveTypeArguments((ParameterizedType) adapterTypeArg, adapterType);
        } else if (adapterTypeArg instanceof TypeVariable) {
            LinkedList<Type> chain = new LinkedList<>();
            chain.add(adapterType);
            return ReflectionUtils.resolveItemVariableType(chain, (TypeVariable<?>) adapterTypeArg/*, true*/);
        } else {
            return adapterTypeArg;
        }
    }

}
