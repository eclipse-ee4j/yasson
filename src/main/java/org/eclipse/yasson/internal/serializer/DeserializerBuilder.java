/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
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
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.ComponentMatcher;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.model.customization.ComponentBoundCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.model.customization.PropertyCustomization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Builder for currently processed items by unmarshaller.
 */
public class DeserializerBuilder extends AbstractSerializerBuilder<DeserializerBuilder> {

    /**
     * Value type of JSON event.
     */
    private JsonParser.Event jsonEvent;

    /**
     * Creates a new builder.
     *
     * @param jsonbContext Context.
     */
    public DeserializerBuilder(JsonbContext jsonbContext) {
        super(jsonbContext);
    }

    /**
     * Sets value type.
     *
     * @param event last json event for constructed deserializer.
     * @return Updated object.
     */
    public DeserializerBuilder withJsonValueType(JsonParser.Event event) {
        this.jsonEvent = event;
        return this;
    }

    /**
     * Build an fully initialized item.
     *
     * @return built item
     */
    public JsonbDeserializer<?> build() {
        withRuntimeType(resolveRuntimeType());
        Class<?> rawType = ReflectionUtils.getRawType(getRuntimeType());

        Optional<AdapterBinding> adapterInfoOptional = Optional.empty();
        Customization customization = getCustomization();
        if (customization == null
                || customization instanceof ComponentBoundCustomization) {
            ComponentBoundCustomization componentBoundCustomization = (ComponentBoundCustomization) customization;

            //First check if user deserializer is registered for such type
            final ComponentMatcher componentMatcher = getJsonbContext().getComponentMatcher();
            Optional<DeserializerBinding<?>> userDeserializer =
                    componentMatcher.getDeserializerBinding(getRuntimeType(), componentBoundCustomization);
            if (userDeserializer.isPresent()) {
                return new UserDeserializerDeserializer<>(this, userDeserializer.get());
            }

            //Second user components is registered.
            Optional<AdapterBinding> adapterBinding = componentMatcher
                    .getDeserializeAdapterBinding(getRuntimeType(), componentBoundCustomization);
            if (adapterBinding.isPresent()) {
                adapterInfoOptional = adapterBinding;
                withRuntimeType(adapterInfoOptional.get().getToType());
                withWrapper(new AdaptedObjectDeserializer<>(adapterInfoOptional.get(),
                                                            (AbstractContainerDeserializer<?>) getWrapper()));
                rawType = ReflectionUtils.getRawType(getRuntimeType());
            }
        }

        if (Optional.class == rawType) {
            return new OptionalObjectDeserializer(this);
        }

        //In case of Base64 json value would be string and recognition by JsonValueType would not work
        if (isByteArray(rawType)) {
            String strategy = getJsonbContext().getConfigProperties().getBinaryDataStrategy();
            switch (strategy) {
            case BinaryDataStrategy.BYTE:
                return new ByteArrayDeserializer(this);
            default:
                return new ByteArrayBase64Deserializer(customization);
            }
        }

        if (isCharArray(rawType)) {
            return new CharArrayDeserializer(this);
        }

        //Third deserializer is a supported value type to deserialize to JSON_VALUE
        if (isJsonValueEvent(jsonEvent)) {
            final Optional<AbstractValueTypeDeserializer<?>> supportedTypeDeserializer = getSupportedTypeDeserializer(rawType);
            if (!supportedTypeDeserializer.isPresent()) {
                if (jsonEvent == JsonParser.Event.VALUE_NULL) {
                    return NullDeserializer.INSTANCE;
                }
                throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR, getRuntimeType()));
            }
            return wrapAdapted(adapterInfoOptional, supportedTypeDeserializer.get());
        }

        JsonbDeserializer<?> deserializer;
        if (jsonEvent == JsonParser.Event.START_ARRAY) {
            if (JsonValue.class.isAssignableFrom(rawType)) {
                return wrapAdapted(adapterInfoOptional, new JsonArrayDeserializer(this));
            } else if (Map.class.isAssignableFrom(rawType)) {
                final JsonbDeserializer<?> mapDeserializer = new MapEntriesArrayDeserializer<>(this);
                return wrapAdapted(adapterInfoOptional, mapDeserializer);
            } else if (rawType.isArray() || getRuntimeType() instanceof GenericArrayType) {
                deserializer = createArrayItem(rawType.getComponentType());
                return wrapAdapted(adapterInfoOptional, deserializer);
            } else if (Collection.class.isAssignableFrom(rawType)) {
                deserializer = new CollectionDeserializer<>(this);
                return wrapAdapted(adapterInfoOptional, deserializer);
            } else {
                throw new JsonbException("Can't deserialize JSON array into: " + getRuntimeType());
            }
        } else if (jsonEvent == JsonParser.Event.START_OBJECT) {
            if (JsonValue.class.isAssignableFrom(rawType)) {
                return wrapAdapted(adapterInfoOptional, new JsonObjectDeserializer(this));
            } else if (Map.class.isAssignableFrom(rawType)) {
                final JsonbDeserializer<?> mapDeserializer = new MapDeserializer<>(this);
                return wrapAdapted(adapterInfoOptional, mapDeserializer);
            } else if (rawType.isInterface()) {
                Class<?> mappedType = getInterfaceMappedType(rawType);
                if (mappedType == null) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.INFER_TYPE_FOR_UNMARSHALL, rawType.getName()));
                }
                withRuntimeType(mappedType);
                withClassModel(getClassModel(mappedType));
                return new ObjectDeserializer<>(this);
            } else {
                if (adapterInfoOptional.isPresent()) {
                    withRuntimeType(adapterInfoOptional.get().getToType());
                    rawType = ReflectionUtils.getRawType(getRuntimeType());
                }

                withClassModel(getClassModel(rawType));

                deserializer = new ObjectDeserializer<>(this);
                return wrapAdapted(adapterInfoOptional, deserializer);
            }
        }
        throw new JsonbException("unresolved type for deserialization: " + getRuntimeType());
    }

    /**
     * Checks if event is a value event.
     *
     * @param event JSON event to check.
     * @return True if one of value events.
     */
    public static boolean isJsonValueEvent(JsonParser.Event event) {
        switch (event) {
        case VALUE_NULL:
        case VALUE_FALSE:
        case VALUE_TRUE:
        case VALUE_NUMBER:
        case VALUE_STRING:
            return true;
        default:
            return false;
        }
    }

    private Optional<AbstractValueTypeDeserializer<?>> getSupportedTypeDeserializer(Class<?> rawType) {
        final Optional<? extends SerializerProviderWrapper> supportedTypeDeserializerOptional = DefaultSerializers.getInstance()
                .findValueSerializerProvider(rawType);
        if (supportedTypeDeserializerOptional.isPresent()) {
            return Optional.of(supportedTypeDeserializerOptional.get().getDeserializerProvider()
                                       .provideDeserializer(getCustomization()));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private JsonbDeserializer<?> wrapAdapted(Optional<AdapterBinding> adapterInfoOptional, JsonbDeserializer<?> item) {
        final Optional<JsonbDeserializer<?>> adaptedDeserializerOptional = adapterInfoOptional.map(adapterInfo -> {
            setAdaptedItemCaptor((AdaptedObjectDeserializer) getWrapper(), item);
            return (JsonbDeserializer<?>) getWrapper();
        });
        return adaptedDeserializerOptional.orElse(item);
    }

    private <T, A> void setAdaptedItemCaptor(AdaptedObjectDeserializer<T, A> decoratorItem, JsonbDeserializer<T> adaptedItem) {
        decoratorItem.setAdaptedTypeDeserializer(adaptedItem);
    }

    private Type resolveRuntimeType() {
        Type result = ReflectionUtils.resolveType(getWrapper(), getGenericType() != null ? getGenericType() : getRuntimeType());
        //Try to infer best from JSON event.
        if (result == Object.class) {
            switch (jsonEvent) {
            case VALUE_FALSE:
            case VALUE_TRUE:
                return Boolean.class;
            case VALUE_NUMBER:
                return BigDecimal.class;
            case VALUE_STRING:
                return String.class;
            case START_ARRAY:
                return ArrayList.class;
            case START_OBJECT:
                return getJsonbContext().getConfigProperties().getDefaultMapImplType();
            case VALUE_NULL:
                return Object.class;
            default:
                throw new IllegalStateException("Can't infer deserialization type type: " + jsonEvent);

            }
        }
        return result;
    }

    private Class<?> getInterfaceMappedType(Class<?> interfaceType) {
        if (interfaceType.isInterface()) {
            Class<?> implementationClass = null;
            //annotation
            if (getCustomization() instanceof PropertyCustomization) {
                implementationClass = ((PropertyCustomization) getCustomization()).getImplementationClass();
            }
            //JsonbConfig
            if (implementationClass == null) {
                implementationClass = getJsonbContext().getConfigProperties().getUserTypeMapping().get(interfaceType);
            }
            if (implementationClass != null) {
                if (!interfaceType.isAssignableFrom(implementationClass)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.IMPL_CLASS_INCOMPATIBLE,
                                                                 implementationClass,
                                                                 interfaceType));
                }
                return implementationClass;
            }
        }
        return null;
    }

    /**
     * Instance is not created in case of array items, because, we don't know how long it should be
     * till parser ends parsing.
     */
    private JsonbDeserializer<?> createArrayItem(Class<?> componentType) {
        if (componentType == byte.class) {
            return new ByteArrayDeserializer(this);
        } else if (componentType == short.class) {
            return new ShortArrayDeserializer(this);
        } else if (componentType == int.class) {
            return new IntArrayDeserializer(this);
        } else if (componentType == long.class) {
            return new LongArrayDeserializer(this);
        } else if (componentType == float.class) {
            return new FloatArrayDeserializer(this);
        } else if (componentType == double.class) {
            return new DoubleArrayDeserializer(this);
        } else {
            return new ObjectArrayDeserializer(this);
        }
    }

    private boolean isByteArray(Class<?> rawType) {
        return rawType.isArray() && rawType.getComponentType() == Byte.TYPE;
    }

    private boolean isCharArray(Class<?> rawType) {
        return rawType.isArray() && rawType.getComponentType() == Character.TYPE;
    }
}
