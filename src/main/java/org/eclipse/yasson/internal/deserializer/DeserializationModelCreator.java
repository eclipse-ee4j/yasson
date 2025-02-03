/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.JsonbConfigProperties;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.deserializer.types.TypeDeserializers;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.ComponentBoundCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.model.customization.PropertyCustomization;
import org.eclipse.yasson.internal.model.customization.TypeInheritanceConfiguration;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import static jakarta.json.bind.JsonbConfig.PROPERTY_NAMING_STRATEGY;
import static jakarta.json.stream.JsonParser.Event;

/**
 * Creator of the deserialization models for deserialized types.
 * <br>
 * This class servers also as a cache for all previously created model deserializers.
 */
public class DeserializationModelCreator {

    private static final ModelDeserializer<Object> NULL_PROVIDER = (value, context) -> null;
    private static final Map<Class<?>, ModelDeserializer<Object>> DEFAULT_CREATOR_VALUES;
    private static final Set<JsonParser.Event> MAP_KEY_EVENTS = new HashSet<>();

    static {
        MAP_KEY_EVENTS.add(Event.KEY_NAME);
        MAP_KEY_EVENTS.addAll(PositionChecker.Checker.VALUES.getEvents());

        Map<Class<?>, ModelDeserializer<Object>> tmpValuesMap = new HashMap<>();

        tmpValuesMap.put(byte.class, (value, context) -> (byte) 0);
        tmpValuesMap.put(short.class, (value, context) -> (short) 0);
        tmpValuesMap.put(int.class, (value, context) -> 0);
        tmpValuesMap.put(long.class, (value, context) -> 0L);
        tmpValuesMap.put(float.class, (value, context) -> 0.0F);
        tmpValuesMap.put(double.class, (value, context) -> 0.0);
        tmpValuesMap.put(char.class, (value, context) -> '\u0000');
        tmpValuesMap.put(boolean.class, (value, context) -> false);
        tmpValuesMap.put(Optional.class, (value, context) -> Optional.empty());
        tmpValuesMap.put(OptionalInt.class, (value, context) -> OptionalInt.empty());
        tmpValuesMap.put(OptionalLong.class, (value, context) -> OptionalLong.empty());
        tmpValuesMap.put(OptionalDouble.class, (value, context) -> OptionalDouble.empty());

        DEFAULT_CREATOR_VALUES = Map.copyOf(tmpValuesMap);
    }

    private final Map<CachedItem, ModelDeserializer<JsonParser>> models = new ConcurrentHashMap<>();

    private final JsonbContext jsonbContext;
    private final Map<Class<?>, Class<?>> userTypeMapping;

    /**
     * Create new instance.
     *
     * @param jsonbContext jsonb context
     */
    public DeserializationModelCreator(JsonbContext jsonbContext) {
        this.jsonbContext = jsonbContext;
        this.userTypeMapping = jsonbContext.getConfigProperties().getUserTypeMapping();
    }

    /**
     * Starts deserializer creation process.
     *
     * @param type type the deserializer is created for
     * @return created deserializer
     */
    public ModelDeserializer<JsonParser> deserializerChain(Type type) {
        LinkedList<Type> chain = new LinkedList<>();
        ClassModel classModel = jsonbContext.getMappingContext().getOrCreateClassModel(ReflectionUtils.getRawType(type));
        return deserializerChain(chain, type, classModel.getClassCustomization(), classModel);
    }

    private ModelDeserializer<JsonParser> deserializerChain(LinkedList<Type> chain,
                                                            Type type,
                                                            Customization propertyCustomization,
                                                            ClassModel classModel) {
        if (chain.contains(type)) {
            return new CyclicReferenceDeserializer(type);
        }
        try {
            chain.add(type);
            return deserializerChainInternal(chain, type, propertyCustomization, classModel);
        } finally {
            chain.removeLast();
        }
    }

    private ModelDeserializer<JsonParser> deserializerChainInternal(LinkedList<Type> chain,
                                                                    Type type,
                                                                    Customization propertyCustomization,
                                                                    ClassModel classModel) {
        Class<?> rawType = classModel.getType();
        CachedItem cachedItem = createCachedItem(type, propertyCustomization);
        if (models.containsKey(cachedItem)) {
            return models.get(cachedItem);
        } else if (userTypeMapping.containsKey(rawType)) {
            Class<?> userTypeRaw = userTypeMapping.get(rawType);
            ModelDeserializer<JsonParser> deserializer = deserializerChain(userTypeRaw);
            models.put(cachedItem, deserializer);
            return deserializer;
        }
        Optional<AdapterBinding> adapterBinding = adapterBinding(type, (ComponentBoundCustomization) propertyCustomization);
        if (adapterBinding.isPresent()) {
            AdapterBinding adapter = adapterBinding.get();
            Class<?> toType = ReflectionUtils.getRawType(adapter.getToType());
            ClassModel targetModel = jsonbContext.getMappingContext().getOrCreateClassModel(toType);
            ModelDeserializer<JsonParser> typeDeserializer = typeDeserializer(toType,
                                                                              targetModel.getClassCustomization(),
                                                                              JustReturn.instance());
            if (typeDeserializer == null) {
                typeDeserializer = deserializerChain(adapter.getToType());
            }
            ModelDeserializer<JsonParser> targetAdapterModel = typeDeserializer;
            AdapterDeserializer adapterDeserializer = new AdapterDeserializer(adapter, JustReturn.instance());
            ModelDeserializer<JsonParser> adapterDeser = (parser, context) -> {
                Object fromJson = targetAdapterModel.deserialize(parser, context);
                return adapterDeserializer.deserialize(fromJson, context);
            };
            models.put(cachedItem, adapterDeser);
            return adapterDeser;
        }
        ModelDeserializer<JsonParser> typeDeserializer = typeDeserializer(rawType,
                                                                          propertyCustomization,
                                                                          JustReturn.instance());
        if (typeDeserializer != null) {
            models.put(cachedItem, typeDeserializer);
            return typeDeserializer;
        }
        if (Collection.class.isAssignableFrom(rawType)) {
            return createCollectionDeserializer(cachedItem, rawType, chain, propertyCustomization);
        } else if (Map.class.isAssignableFrom(rawType)) {
            return createMapDeserializer(cachedItem, rawType, chain, propertyCustomization);
        } else if (rawType.isArray()) {
            return createArrayDeserializer(cachedItem, rawType, chain, propertyCustomization);
        } else if (type instanceof GenericArrayType) {
            return createGenericArray(cachedItem, rawType, chain, propertyCustomization);
        } else if (Optional.class.isAssignableFrom(rawType)) {
            return createOptionalDeserializer(chain, type, propertyCustomization, cachedItem);
        } else {
            return createObjectDeserializer(chain, type, propertyCustomization, classModel, rawType, cachedItem);
        }
    }

    private ModelDeserializer<JsonParser> createObjectDeserializer(LinkedList<Type> chain,
                                                                   Type type,
                                                                   Customization propertyCustomization,
                                                                   ClassModel classModel,
                                                                   Class<?> rawType,
                                                                   CachedItem cachedItem) {
        ClassCustomization classCustomization = classModel.getClassCustomization();
        Optional<DeserializerBinding<?>> deserializerBinding = userDeserializer(type,
                                                                                (ComponentBoundCustomization) propertyCustomization);
        if (deserializerBinding.isPresent()) {
            UserDefinedDeserializer user = new UserDefinedDeserializer(deserializerBinding.get().getJsonbDeserializer(),
                                                                       JustReturn.instance(), type, classCustomization);
            models.put(cachedItem, user);
            return user;
        }
        JsonbCreator creator = classCustomization.getCreator();
        boolean hasCreator = creator != null;
        List<String> params = hasCreator ? creatorParamsList(creator) : Collections.emptyList();
        Function<String, String> renamer = propertyRenamer();
        Map<String, ModelDeserializer<JsonParser>> processors = new LinkedHashMap<>();
        Map<String, ModelDeserializer<Object>> defaultCreatorValues = new HashMap<>();
        for (PropertyModel propertyModel : classModel.getSortedProperties()) {
            if (!propertyModel.isWritable() || params.contains(propertyModel.getReadName())) {
                continue;
            }
            ModelDeserializer<JsonParser> modelDeserializer = memberTypeProcessor(chain, propertyModel, hasCreator);
            processors.put(renamer.apply(propertyModel.getReadName()), modelDeserializer);
        }
        for (String s : params) {
            CreatorModel creatorModel = creator.findByName(s);
            ModelDeserializer<JsonParser> modelDeserializer = typeProcessor(chain,
                                                                            creatorModel.getType(),
                                                                            creatorModel.getCustomization(),
                                                                            JustReturn.instance());
            String parameterName = renamer.apply(creatorModel.getName());
            processors.put(parameterName, modelDeserializer);
            if (creatorModel.getCustomization().isRequired()) {
                defaultCreatorValues.put(parameterName, new RequiredCreatorParameter(parameterName));
            } else {
                Class<?> rawParamType = ReflectionUtils.getOptionalRawType(creatorModel.getType()).orElse(Object.class);
                defaultCreatorValues.put(parameterName, DEFAULT_CREATOR_VALUES.getOrDefault(rawParamType, NULL_PROVIDER));
            }
        }
        ModelDeserializer<JsonParser> instanceCreator;
        TypeInheritanceConfiguration typeInheritanceConfiguration = classCustomization.getPolymorphismConfig();
        Set<String> ignoredProperties = collectIgnoredProperties(typeInheritanceConfiguration);
        boolean failOnUnknownProperties = jsonbContext.getConfigProperties().getConfigFailOnUnknownProperties();
        if (hasCreator) {
            instanceCreator = new JsonbCreatorDeserializer(processors, defaultCreatorValues, creator, rawType, renamer,
                                                           failOnUnknownProperties, ignoredProperties);
        } else {
            ModelDeserializer<JsonParser> typeWrapper = new ObjectDeserializer(processors, renamer, rawType,
                                                                               failOnUnknownProperties, ignoredProperties);
            instanceCreator = new DefaultObjectInstanceCreator(typeWrapper, rawType,
                                                               classModel.getDefaultConstructor());
        }
        PositionChecker positionChecker = new PositionChecker(instanceCreator, rawType, Event.START_OBJECT);
        if (typeInheritanceConfiguration != null && !typeInheritanceConfiguration.isInherited()) {
            instanceCreator = new InheritanceInstanceCreator(rawType, this, typeInheritanceConfiguration, positionChecker);
            positionChecker = new PositionChecker(instanceCreator, rawType, Event.START_OBJECT);
        }
        ModelDeserializer<JsonParser> nullChecker = new NullCheckDeserializer(positionChecker, JustReturn.instance());
        models.put(cachedItem, nullChecker);
        return nullChecker;
    }

    private ModelDeserializer<JsonParser> createCollectionDeserializer(CachedItem cachedItem,
                                                                       Class<?> rawType,
                                                                       LinkedList<Type> chain,
                                                                       Customization propertyCustomization) {
        Type type = cachedItem.type;
        Type colType = type instanceof ParameterizedType
                ? ((ParameterizedType) type).getActualTypeArguments()[0]
                : Object.class;
        colType = ReflectionUtils.resolveType(chain, colType);
        ClassModel classModel = jsonbContext.getMappingContext().getOrCreateClassModel(ReflectionUtils.getRawType(colType));
        ModelDeserializer<JsonParser> typeProcessor = typeProcessor(chain,
                                                                    colType,
                                                                    classModel.getClassCustomization(),
                                                                    JustReturn.instance());
        CollectionDeserializer collectionDeserializer = new CollectionDeserializer(typeProcessor);
        CollectionInstanceCreator instanceDeserializer = new CollectionInstanceCreator(collectionDeserializer, type);
        PositionChecker positionChecker = new PositionChecker(instanceDeserializer, rawType, Event.START_ARRAY);
        NullCheckDeserializer nullChecker = new NullCheckDeserializer(positionChecker, JustReturn.instance());
        models.put(cachedItem, nullChecker);
        return nullChecker;
    }

    private ModelDeserializer<JsonParser> createMapDeserializer(CachedItem cachedItem,
                                                                Class<?> rawType,
                                                                LinkedList<Type> chain,
                                                                Customization propertyCustomization) {
        Type type = cachedItem.type;
        Type keyType = type instanceof ParameterizedType
                ? ((ParameterizedType) type).getActualTypeArguments()[0]
                : Object.class;
        Type valueType = type instanceof ParameterizedType
                ? ((ParameterizedType) type).getActualTypeArguments()[1]
                : Object.class;
        ModelDeserializer<JsonParser> keyProcessor = typeProcessor(chain,
                                                                   keyType,
                                                                   ClassCustomization.empty(),
                                                                   JustReturn.instance(),
                                                                   MAP_KEY_EVENTS);
        ModelDeserializer<JsonParser> valueProcessor = typeProcessor(chain,
                                                                     valueType,
                                                                     propertyCustomization,
                                                                     JustReturn.instance());

        MapDeserializer mapDeserializer = new MapDeserializer(keyProcessor, valueProcessor);
        MapInstanceCreator mapInstanceCreator = new MapInstanceCreator(mapDeserializer,
                                                                       jsonbContext.getConfigProperties(),
                                                                       rawType);
        PositionChecker positionChecker = new PositionChecker(mapInstanceCreator, rawType, PositionChecker.Checker.CONTAINER);
        NullCheckDeserializer nullChecker = new NullCheckDeserializer(positionChecker, JustReturn.instance());
        models.put(cachedItem, nullChecker);
        return nullChecker;
    }

    private ModelDeserializer<JsonParser> createArrayDeserializer(CachedItem cachedItem,
                                                                  Class<?> rawType,
                                                                  LinkedList<Type> chain,
                                                                  Customization propertyCustomization) {
        JsonbConfigProperties configProperties = jsonbContext.getConfigProperties();
        if (rawType.equals(byte[].class) && !configProperties.getBinaryDataStrategy().equals(BinaryDataStrategy.BYTE)) {
            String strategy = configProperties.getBinaryDataStrategy();
            ModelDeserializer<JsonParser> typeProcessor = typeProcessor(chain,
                                                                        String.class,
                                                                        propertyCustomization,
                                                                        JustReturn.instance());
            ModelDeserializer<JsonParser> base64Deserializer = ArrayInstanceCreator.createBase64Deserializer(strategy,
                                                                                                             typeProcessor);
            NullCheckDeserializer nullChecker = new NullCheckDeserializer(base64Deserializer, JustReturn.instance());
            models.put(cachedItem, nullChecker);
            return nullChecker;
        }
        Class<?> arrayType = rawType.getComponentType();
        ModelDeserializer<JsonParser> typeProcessor = typeProcessor(chain,
                                                                    arrayType,
                                                                    propertyCustomization,
                                                                    JustReturn.instance());
        return createArrayCommonDeserializer(cachedItem, rawType, arrayType, typeProcessor);
    }

    private ModelDeserializer<JsonParser> createGenericArray(CachedItem cachedItem,
                                                             Class<?> rawType,
                                                             LinkedList<Type> chain,
                                                             Customization propertyCustomization) {
        GenericArrayType type = (GenericArrayType) cachedItem.type;
        Class<?> component = ReflectionUtils.getRawType(type.getGenericComponentType());
        ModelDeserializer<JsonParser> typeProcessor = typeProcessor(chain,
                                                                    type.getGenericComponentType(),
                                                                    propertyCustomization,
                                                                    JustReturn.instance());
        return createArrayCommonDeserializer(cachedItem, rawType, component, typeProcessor);
    }

    private ModelDeserializer<JsonParser> createArrayCommonDeserializer(CachedItem cachedItem,
                                                                        Class<?> rawType,
                                                                        Class<?> component,
                                                                        ModelDeserializer<JsonParser> typeProcessor) {
        ArrayDeserializer arrayDeserializer = new ArrayDeserializer(typeProcessor);
        ArrayInstanceCreator arrayInstanceCreator = ArrayInstanceCreator.create(rawType, component, arrayDeserializer);
        PositionChecker positionChecker = new PositionChecker(arrayInstanceCreator, rawType, Event.START_ARRAY);
        NullCheckDeserializer nullChecker = new NullCheckDeserializer(positionChecker, JustReturn.instance());
        models.put(cachedItem, nullChecker);
        return nullChecker;
    }

    private OptionalDeserializer createOptionalDeserializer(LinkedList<Type> chain,
                                                            Type type,
                                                            Customization propertyCustomization,
                                                            CachedItem cachedItem) {
        Type colType = type instanceof ParameterizedType
                ? ((ParameterizedType) type).getActualTypeArguments()[0]
                : Object.class;
        ModelDeserializer<JsonParser> typeProcessor = typeProcessor(chain, colType, propertyCustomization, JustReturn.instance());
        OptionalDeserializer optionalDeserializer = new OptionalDeserializer(typeProcessor, JustReturn.instance());
        models.put(cachedItem, optionalDeserializer);
        return optionalDeserializer;
    }

    private Set<String> collectIgnoredProperties(TypeInheritanceConfiguration typeInheritanceConfiguration) {
        Set<String> ignoredProperties = new HashSet<>();
        if (typeInheritanceConfiguration != null) {
            TypeInheritanceConfiguration current = typeInheritanceConfiguration;
            while (current != null) {
                ignoredProperties.add(current.getFieldName());
                current = current.getParentConfig();
            }
        }
        return ignoredProperties;
    }

    private Function<String, String> propertyRenamer() {
        boolean isCaseInsensitive = jsonbContext.getConfig()
                .getProperty(PROPERTY_NAMING_STRATEGY)
                .filter(prop -> prop.equals(PropertyNamingStrategy.CASE_INSENSITIVE))
                .isPresent();

        return isCaseInsensitive
                ? String::toLowerCase
                : value -> value;
    }

    private Optional<AdapterBinding> adapterBinding(Type type, ComponentBoundCustomization classCustomization) {
        return jsonbContext.getComponentMatcher().getDeserializeAdapterBinding(type, classCustomization);
    }

    private Optional<DeserializerBinding<?>> userDeserializer(Type type, ComponentBoundCustomization classCustomization) {
        return jsonbContext.getComponentMatcher().getDeserializerBinding(type, classCustomization);
    }

    private List<String> creatorParamsList(JsonbCreator creator) {
        return Arrays.stream(creator.getParams()).map(CreatorModel::getName).collect(Collectors.toList());
    }

    private ModelDeserializer<JsonParser> memberTypeProcessor(LinkedList<Type> chain,
                                                              PropertyModel propertyModel,
                                                              boolean hasCreator) {
        ModelDeserializer<Object> memberDeserializer;
        Type type = propertyModel.getPropertyDeserializationType();
        memberDeserializer = new ValueSetterDeserializer(propertyModel.getSetValueHandle());
        if (hasCreator) {
            memberDeserializer = new DeferredDeserializer(memberDeserializer);
        }
        return typeProcessor(chain, type, propertyModel.getCustomization(), memberDeserializer);
    }

    private ModelDeserializer<JsonParser> typeProcessor(LinkedList<Type> chain,
                                                        Type type,
                                                        Customization customization,
                                                        ModelDeserializer<Object> memberDeserializer) {
        return typeProcessor(chain, type, customization, memberDeserializer, PositionChecker.Checker.VALUES.getEvents());
    }

    private ModelDeserializer<JsonParser> typeProcessor(LinkedList<Type> chain,
                                                        Type type,
                                                        Customization customization,
                                                        ModelDeserializer<Object> memberDeserializer,
                                                        Set<Event> events) {
        Type resolved = ReflectionUtils.resolveType(chain, type);
        Class<?> rawType = ReflectionUtils.getRawType(resolved);
        Optional<DeserializerBinding<?>> deserializerBinding = userDeserializer(resolved,
                                                                                (ComponentBoundCustomization) customization);
        if (deserializerBinding.isPresent()) {
            //TODO remove or not? fix for deserializer cycle
            //            ModelDeserializer<JsonParser> exactType = createNewChain(chain, memberDeserializer, rawType,
            //            resolved, customization);
            //            return new UserDefinedDeserializer(deserializerBinding.get().getJsonbDeserializer(),
            //                                               exactType,
            //                                               memberDeserializer,
            //                                               resolved,
            //                                               customization);
            return new UserDefinedDeserializer(deserializerBinding.get().getJsonbDeserializer(),
                                               memberDeserializer,
                                               resolved,
                                               customization);
        }
        Optional<AdapterBinding> adapterBinding = adapterBinding(resolved, (ComponentBoundCustomization) customization);
        if (adapterBinding.isPresent()) {
            AdapterBinding adapter = adapterBinding.get();
            ModelDeserializer<JsonParser> typeDeserializer = typeDeserializer(ReflectionUtils.getRawType(adapter.getToType()),
                                                                              customization,
                                                                              JustReturn.instance(), events);
            if (typeDeserializer == null) {
                typeDeserializer = deserializerChain(adapter.getToType());
            }
            ModelDeserializer<JsonParser> targetAdapterModel = typeDeserializer;

            AdapterDeserializer adapterDeserializer = new AdapterDeserializer(adapter, memberDeserializer);
            return (parser, context) -> {
                DeserializationContextImpl newContext = new DeserializationContextImpl(context);
                Object fromJson = targetAdapterModel.deserialize(parser, newContext);
                return adapterDeserializer.deserialize(fromJson, context);
            };
        }
        ModelDeserializer<JsonParser> typeDeserializer = typeDeserializer(rawType, customization, memberDeserializer, events);
        if (typeDeserializer == null) {
            Class<?> implClass = resolveImplClass(rawType, customization);
            return createNewChain(chain, memberDeserializer, implClass, resolved, customization);
        }
        return typeDeserializer;
    }

    private ModelDeserializer<JsonParser> createNewChain(LinkedList<Type> chain,
                                                         ModelDeserializer<Object> memberDeserializer,
                                                         Class<?> rawType,
                                                         Type type,
                                                         Customization propertyCustomization) {
        ClassModel classModel = jsonbContext.getMappingContext().getOrCreateClassModel(rawType);
        ModelDeserializer<JsonParser> modelDeserializer = deserializerChain(chain, type, propertyCustomization, classModel);
        return new ContextSwitcher(memberDeserializer, modelDeserializer);
    }

    private ModelDeserializer<JsonParser> typeDeserializer(Class<?> rawType,
                                                           Customization customization,
                                                           ModelDeserializer<Object> delegate) {
        return typeDeserializer(rawType, customization, delegate, PositionChecker.Checker.VALUES.getEvents());
    }

    private ModelDeserializer<JsonParser> typeDeserializer(Class<?> rawType,
                                                           Customization customization,
                                                           ModelDeserializer<Object> delegate,
                                                           Set<JsonParser.Event> events) {
        return TypeDeserializers
                .getTypeDeserializer(rawType, customization, jsonbContext.getConfigProperties(), delegate, events);
    }

    private Class<?> resolveImplClass(Class<?> rawType, Customization customization) {
        if (rawType.isInterface()) {
            Class<?> implementationClass = null;
            //annotation
            if (customization instanceof PropertyCustomization) {
                implementationClass = ((PropertyCustomization) customization).getImplementationClass();
            }
            //JsonbConfig
            if (implementationClass == null) {
                implementationClass = jsonbContext.getConfigProperties().getUserTypeMapping().get(rawType);
            }
            if (implementationClass != null) {
                if (!rawType.isAssignableFrom(implementationClass)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.IMPL_CLASS_INCOMPATIBLE,
                                                                 implementationClass,
                                                                 rawType));
                }
                return implementationClass;
            }
        }
        return rawType;
    }

    private CachedItem createCachedItem(Type type, Customization customization) {
        return new CachedItem(type, customization.getDeserializeNumberFormatter(), customization.getDeserializeDateFormatter());
    }

    private static final class CachedItem {

        private final Type type;
        private final JsonbNumberFormatter numberFormatter;
        private final JsonbDateFormatter dateFormatter;

        CachedItem(Type type, JsonbNumberFormatter numberFormatter, JsonbDateFormatter dateFormatter) {
            this.type = type;
            this.numberFormatter = numberFormatter;
            this.dateFormatter = dateFormatter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CachedItem that = (CachedItem) o;
            return Objects.equals(type, that.type)
                    && Objects.equals(numberFormatter, that.numberFormatter)
                    && Objects.equals(dateFormatter, that.dateFormatter);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, numberFormatter, dateFormatter);
        }

        @Override
        public String toString() {
            return "CachedItem{"
                    + "type=" + type
                    + ", numberFormatter=" + numberFormatter
                    + ", dateFormatter=" + dateFormatter
                    + '}';
        }
    }

}
