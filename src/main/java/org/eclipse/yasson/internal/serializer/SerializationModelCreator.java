/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.json.bind.JsonbException;

import org.eclipse.yasson.internal.ComponentMatcher;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.ComponentBoundCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.model.customization.TypeInheritanceConfiguration;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.types.ObjectTypeSerializer;
import org.eclipse.yasson.internal.serializer.types.TypeSerializers;

/**
 * Create or obtain already created type serializer.
 */
public class SerializationModelCreator {

    private final Map<Type, ModelSerializer> explicitChain = new ConcurrentHashMap<>();
    private final Map<Type, ModelSerializer> dynamicChain = new ConcurrentHashMap<>();
    private final JsonbContext jsonbContext;

    /**
     * Create new instance.
     *
     * @param jsonbContext jsonb context
     */
    public SerializationModelCreator(JsonbContext jsonbContext) {
        this.jsonbContext = jsonbContext;
    }

    /**
     * Wrap {@link ModelSerializer} in the common set of serializers.
     *
     * @param modelSerializer serializer to be wrapped
     * @param customization   component customization
     * @param jsonbContext    jsonb context
     * @return wrapped serializer
     */
    public static ModelSerializer wrapInCommonSet(ModelSerializer modelSerializer,
                                                  Customization customization,
                                                  JsonbContext jsonbContext) {
        KeyWriter serializer = new KeyWriter(modelSerializer);
        return new NullSerializer(serializer, customization, jsonbContext);
    }

    /**
     * Create new {@link ModelSerializer} of the given type.
     *
     * @param type               type to be serialized
     * @param rootValue          whether it is a root value
     * @param resolveRootAdapter whether to resolve root adapter
     * @return type model serializer
     */
    public ModelSerializer serializerChain(Type type, boolean rootValue, boolean resolveRootAdapter) {
        Class<?> rawType = ReflectionUtils.getRawType(type);
        ClassModel classModel = jsonbContext.getMappingContext().getOrCreateClassModel(rawType);
        LinkedList<Type> chain = new LinkedList<>();
        return serializerChain(chain, type, classModel.getClassCustomization(), rootValue, false, resolveRootAdapter);
    }

    /**
     * Create new {@link ModelSerializer} of the given type.
     *
     * @param chain                 chain of types used before the one currently processed
     * @param type                  type to be serialized
     * @param propertyCustomization component customization
     * @param rootValue             whether it is a root value
     * @param isKey                 whether it is a key
     * @return type model serializer
     */
    public ModelSerializer serializerChainRuntime(LinkedList<Type> chain,
                                                  Type type,
                                                  Customization propertyCustomization,
                                                  boolean rootValue,
                                                  boolean isKey) {
        if (chain.contains(type)) {
            return new CyclicReferenceSerializer(type);
        }
        //If the class instance and class of the field are the same and there has been generics specified for this field,
        //we need to use those instead of raw type.
        Class<?> rawType = ReflectionUtils.getRawType(type);
        Class<?> rawLast = ReflectionUtils.getRawType(chain.getLast());
        if (rawLast.equals(rawType)) {
            return serializerChainInternal(chain, chain.getLast(), propertyCustomization, rootValue, isKey, true);
        }
        return serializerChainInternal(chain, type, propertyCustomization, rootValue, isKey, true);
    }

    private ModelSerializer serializerChain(LinkedList<Type> chain,
                                            Type type,
                                            Customization propertyCustomization,
                                            boolean rootValue,
                                            boolean isKey,
                                            boolean resolveRootAdapter) {
        if (chain.contains(type)) {
            return new CyclicReferenceSerializer(type);
        }
        try {
            chain.add(type);
            return serializerChainInternal(chain, type, propertyCustomization, rootValue, isKey, resolveRootAdapter);
        } finally {
            chain.removeLast();
        }
    }

    private ModelSerializer serializerChainInternal(LinkedList<Type> chain,
                                                    Type type,
                                                    Customization propertyCustomization,
                                                    boolean rootValue,
                                                    boolean isKey,
                                                    boolean resolveRootAdapter) {
        if (explicitChain.containsKey(type)) {
            return explicitChain.get(type);
        }
        Class<?> rawType = ReflectionUtils.getRawType(type);
        Optional<ModelSerializer> serializerBinding = userSerializer(type,
                                                                     (ComponentBoundCustomization) propertyCustomization);
        if (serializerBinding.isPresent()) {
            return serializerBinding.get();
        }
        if (resolveRootAdapter) {
            Optional<AdapterBinding> maybeAdapter = adapterBinding(type, (ComponentBoundCustomization) propertyCustomization);
            if (maybeAdapter.isPresent()) {
                AdapterBinding adapterBinding = maybeAdapter.get();
                Type toType = adapterBinding.getToType();
                Class<?> rawToType = ReflectionUtils.getRawType(toType);
                ModelSerializer typeSerializer = TypeSerializers
                        .getTypeSerializer(rawToType, propertyCustomization, jsonbContext);
                if (typeSerializer == null) {
                    typeSerializer = serializerChain(toType, rootValue, !type.equals(toType));
                }
                AdapterSerializer adapterSerializer = new AdapterSerializer(adapterBinding, typeSerializer);
                RecursionChecker recursionChecker = new RecursionChecker(adapterSerializer);
                NullSerializer nullSerializer = new NullSerializer(recursionChecker, propertyCustomization, jsonbContext);
                explicitChain.put(type, nullSerializer);
                return nullSerializer;
            }
        }

        ModelSerializer typeSerializer = null;
        if (!Object.class.equals(rawType)) {
            typeSerializer = TypeSerializers.getTypeSerializer(chain, rawType, propertyCustomization, jsonbContext, isKey);
        }
        if (typeSerializer != null) {
            if (jsonbContext.getConfigProperties().isStrictIJson() && rootValue) {
                throw new JsonbException(Messages.getMessage(MessageKeys.IJSON_ENABLED_SINGLE_VALUE));
            }
            return typeSerializer;
        }
        ClassModel classModel = jsonbContext.getMappingContext().getOrCreateClassModel(rawType);
        if (Collection.class.isAssignableFrom(rawType)) {
            return createCollectionSerializer(chain, type, propertyCustomization);
        } else if (Map.class.isAssignableFrom(rawType)) {
            return createMapSerializer(chain, type, propertyCustomization);
        } else if (rawType.isArray()) {
            return createArraySerializer(chain, rawType, propertyCustomization);
        } else if (type instanceof GenericArrayType) {
            return createGenericArraySerializer(chain, type, propertyCustomization);
        } else if (Optional.class.equals(rawType)) {
            return createOptionalSerializer(chain, type, propertyCustomization, isKey);
        }
        return createObjectSerializer(chain, type, classModel);
    }

    private ModelSerializer createObjectSerializer(LinkedList<Type> chain,
                                                   Type type,
                                                   ClassModel classModel) {
        LinkedHashMap<String, ModelSerializer> propertySerializers = new LinkedHashMap<>();
        TypeInheritanceConfiguration typeInheritanceConfiguration = classModel.getClassCustomization().getPolymorphismConfig();
        if (typeInheritanceConfiguration != null) {
            addPolymorphismProperty(typeInheritanceConfiguration, propertySerializers, classModel);
        }
        for (PropertyModel model : classModel.getSortedProperties()) {
            if (model.isReadable()) {
                String name = model.getWriteName();
                ModelSerializer memberModel = memberSerializer(chain,
                                                               model.getPropertySerializationType(),
                                                               model.getCustomization(),
                                                               false);
                propertySerializers.put(name, new ValueGetterSerializer(model.getGetValueHandle(), memberModel));
            }
        }
        ModelSerializer objectSerializer = new ObjectSerializer(propertySerializers);
        RecursionChecker recursionChecker = new RecursionChecker(objectSerializer);
        KeyWriter keyWriter = new KeyWriter(recursionChecker);
        NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(false, keyWriter);
        NullSerializer nullSerializer = new NullSerializer(nullVisibilitySwitcher, classModel.getClassCustomization(),
                                                           jsonbContext);
        explicitChain.put(type, nullSerializer);
        return nullSerializer;
    }

    private void addPolymorphismProperty(TypeInheritanceConfiguration typeInheritanceConfiguration,
                                         LinkedHashMap<String, ModelSerializer> propertySerializers,
                                         ClassModel classModel) {
        Class<?> rawType = classModel.getType();
        String alias = typeInheritanceConfiguration.getAliases().get(rawType);
        ModelSerializer serializer = createPolymorphismPropertySerializer(typeInheritanceConfiguration, alias);
        if ((!typeInheritanceConfiguration.isInherited() || alias != null) && typeInheritanceConfiguration.getParentConfig() != null) {
            addParentPolymorphismProperty(typeInheritanceConfiguration.getParentConfig(), propertySerializers, classModel);
        }
        if (serializer != null) {
            propertySerializers.put(typeInheritanceConfiguration.getFieldName(), serializer);
        }
        for (PropertyModel propertyModel : classModel.getSortedProperties()) {
            if (propertySerializers.containsKey(propertyModel.getWriteName())) {
                throw new JsonbException("CHANGE naming conflict!");
            }
        }
    }

    private void addParentPolymorphismProperty(TypeInheritanceConfiguration typeInheritanceConfiguration,
                                               LinkedHashMap<String, ModelSerializer> propertySerializers,
                                               ClassModel classModel) {
        Class<?> rawType = classModel.getType();
        TypeInheritanceConfiguration current = typeInheritanceConfiguration;
        LinkedHashMap<String, ModelSerializer> toBeAdded = new LinkedHashMap<>();
        while (current != null) {
            TypeInheritanceConfiguration local = current;
            String alias = local.getAliases().entrySet().stream()
                    .filter(entry -> entry.getKey().isAssignableFrom(rawType))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
            if (alias != null) {
                ModelSerializer serializer = createPolymorphismPropertySerializer(local, alias);
                toBeAdded.put(current.getFieldName(), serializer);
                current = current.getParentConfig();
            }
        }
        ListIterator<Map.Entry<String, ModelSerializer>> iterator = new ArrayList<>(toBeAdded.entrySet())
                .listIterator(toBeAdded.size());
        while (iterator.hasPrevious()) {
            Map.Entry<String, ModelSerializer> entry = iterator.previous();
            propertySerializers.put(entry.getKey(), entry.getValue());
        }
    }

    private ModelSerializer createPolymorphismPropertySerializer(TypeInheritanceConfiguration configuration, String alias) {
        if (alias != null) {
            return (value, generator, context) -> generator.write(configuration.getFieldName(), alias);
        }
        return null;
    }

    private ModelSerializer createCollectionSerializer(LinkedList<Type> chain,
                                                       Type type,
                                                       Customization customization) {
        Type colType = type instanceof ParameterizedType
                ? ((ParameterizedType) type).getActualTypeArguments()[0]
                : Object.class;
        Type resolvedKey = ReflectionUtils.resolveType(chain, colType);
        Class<?> rawClass = ReflectionUtils.getRawType(resolvedKey);
        ClassModel classModel = jsonbContext.getMappingContext().getOrCreateClassModel(rawClass);
        ModelSerializer typeSerializer = memberSerializer(chain, colType, classModel.getClassCustomization(), false);
        CollectionSerializer collectionSerializer = new CollectionSerializer(typeSerializer);
        KeyWriter keyWriter = new KeyWriter(collectionSerializer);
        NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(true, keyWriter);
        return new NullSerializer(nullVisibilitySwitcher, customization, jsonbContext);
    }

    private ModelSerializer createMapSerializer(LinkedList<Type> chain, Type type, Customization propertyCustomization) {
        Type keyType = type instanceof ParameterizedType
                ? ((ParameterizedType) type).getActualTypeArguments()[0]
                : Object.class;
        Type valueType = type instanceof ParameterizedType
                ? ((ParameterizedType) type).getActualTypeArguments()[1]
                : Object.class;
        Type resolvedKey = ReflectionUtils.resolveType(chain, keyType);
        Class<?> rawClass = ReflectionUtils.getRawType(resolvedKey);
        ModelSerializer keySerializer = memberSerializer(chain, keyType, ClassCustomization.empty(), true);
        ModelSerializer valueSerializer = memberSerializer(chain, valueType, propertyCustomization, false);
        MapSerializer mapSerializer = MapSerializer.create(rawClass, keySerializer, valueSerializer);
        KeyWriter keyWriter = new KeyWriter(mapSerializer);
        NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(true, keyWriter);
        return new NullSerializer(nullVisibilitySwitcher, propertyCustomization, jsonbContext);
    }

    private ModelSerializer createArraySerializer(LinkedList<Type> chain,
                                                  Class<?> raw,
                                                  Customization propertyCustomization) {
        Class<?> arrayComponent = raw.getComponentType();
        ModelSerializer modelSerializer = memberSerializer(chain, arrayComponent, propertyCustomization, false);
        ModelSerializer arraySerializer = ArraySerializer.create(raw, jsonbContext, modelSerializer);
        KeyWriter keyWriter = new KeyWriter(arraySerializer);
        NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(true, keyWriter);
        return new NullSerializer(nullVisibilitySwitcher, propertyCustomization, jsonbContext);
    }

    private ModelSerializer createGenericArraySerializer(LinkedList<Type> chain,
                                                         Type type,
                                                         Customization propertyCustomization) {
        Class<?> raw = ReflectionUtils.getRawType(type);
        Class<?> component = ReflectionUtils.getRawType(((GenericArrayType) type).getGenericComponentType());
        ModelSerializer modelSerializer = memberSerializer(chain, component, propertyCustomization, false);
        ModelSerializer arraySerializer = ArraySerializer.create(raw, jsonbContext, modelSerializer);
        KeyWriter keyWriter = new KeyWriter(arraySerializer);
        NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(true, keyWriter);
        return new NullSerializer(nullVisibilitySwitcher, propertyCustomization, jsonbContext);
    }

    private ModelSerializer createOptionalSerializer(LinkedList<Type> chain,
                                                     Type type,
                                                     Customization propertyCustomization,
                                                     boolean isKey) {
        Type optType = type instanceof ParameterizedType
                ? ((ParameterizedType) type).getActualTypeArguments()[0]
                : Object.class;
        ModelSerializer modelSerializer = memberSerializer(chain, optType, propertyCustomization, isKey);
        return new OptionalSerializer(modelSerializer);
    }

    private ModelSerializer memberSerializer(LinkedList<Type> chain,
                                             Type type,
                                             Customization customization,
                                             boolean key) {
        Type resolved = ReflectionUtils.resolveType(chain, type);
        Class<?> rawType = ReflectionUtils.getRawType(resolved);

        Optional<ModelSerializer> serializerBinding = userSerializer(resolved,
                                                                     (ComponentBoundCustomization) customization);
        if (serializerBinding.isPresent()) {
            return serializerBinding.get();
        }
        Optional<AdapterBinding> maybeAdapter = adapterBinding(resolved, (ComponentBoundCustomization) customization);
        if (maybeAdapter.isPresent()) {
            AdapterBinding adapterBinding = maybeAdapter.get();
            Type toType = adapterBinding.getToType();
            Class<?> rawToType = ReflectionUtils.getRawType(toType);
            ModelSerializer typeSerializer = TypeSerializers.getTypeSerializer(rawToType, customization, jsonbContext);
            if (typeSerializer == null) {
                typeSerializer = serializerChain(toType, false, true);
            }
            AdapterSerializer adapterSerializer = new AdapterSerializer(adapterBinding, typeSerializer);
            return new NullSerializer(adapterSerializer, customization, jsonbContext);
        }
        ModelSerializer typeSerializer = TypeSerializers.getTypeSerializer(chain, rawType, customization, jsonbContext, key);
        if (typeSerializer == null) {
            //Final classes dont have any child classes. It is safe to assume that there will be instance of that specific class.
            boolean isFinal = Modifier.isFinal(rawType.getModifiers());
            if (isFinal
                    || Collection.class.isAssignableFrom(rawType)
                    || Map.class.isAssignableFrom(rawType)) {
                return serializerChain(chain, resolved, customization, false, key, true);
            } else {
                if (dynamicChain.containsKey(resolved)) {
                    return dynamicChain.get(resolved);
                }
                boolean isAbstract = Modifier.isAbstract(rawType.getModifiers());
                ModelSerializer specificTypeSerializer = null;
                if (!isAbstract && !rawType.equals(Object.class)) {
                    if (explicitChain.containsKey(resolved)) {
                        specificTypeSerializer = explicitChain.get(resolved);
                    } else {
                        specificTypeSerializer = serializerChain(chain, resolved, customization, false, key, true);
                    }
                }
                //Needs to be dynamically resolved with special cache since possible inheritance problem.
                if (resolved instanceof Class) {
                    typeSerializer = TypeSerializers.getTypeSerializer(chain, Object.class, customization, jsonbContext, key);
                } else {
                    chain.add(resolved);
                    typeSerializer = TypeSerializers.getTypeSerializer(chain, Object.class, customization, jsonbContext, key);
                    chain.removeLast();
                }
                if (specificTypeSerializer != null && typeSerializer instanceof ObjectTypeSerializer) {
                    ((ObjectTypeSerializer) typeSerializer).addSpecificSerializer(rawType, specificTypeSerializer);
                }
                //Since typeSerializer is handled as Object currently, we need to wrap it with null checker (if it is not a key)
                if (!key) {
                    typeSerializer = new NullSerializer(typeSerializer, customization, jsonbContext);
                }

                dynamicChain.put(type, typeSerializer);
            }
        }
        if (!key && typeSerializer instanceof ObjectTypeSerializer) {
            typeSerializer = new NullSerializer(typeSerializer, customization, jsonbContext);
        }
        return typeSerializer;
    }

    private Optional<ModelSerializer> userSerializer(Type type, ComponentBoundCustomization classCustomization) {
        final ComponentMatcher componentMatcher = jsonbContext.getComponentMatcher();
        return componentMatcher.getSerializerBinding(type, classCustomization)
                .map(SerializerBinding::getJsonbSerializer)
                .map(UserDefinedSerializer::new)
                .map(RecursionChecker::new)
                .map(serializer -> SerializationModelCreator.wrapInCommonSet(serializer,
                                                                             (Customization) classCustomization,
                                                                             jsonbContext));
    }

    private Optional<AdapterBinding> adapterBinding(Type type, ComponentBoundCustomization classCustomization) {
        return jsonbContext.getComponentMatcher().getSerializeAdapterBinding(type, classCustomization);
    }

}
