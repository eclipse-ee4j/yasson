/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.AbstractItem;
import org.eclipse.yasson.internal.serializer.EmbeddedItem;
import org.eclipse.yasson.internal.serializer.ResolvedParameterizedType;

/**
 * Utility class for resolution of generics during unmarshalling.
 */
public class ReflectionUtils {

    private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class.getName());

    private ReflectionUtils() {
        throw new IllegalStateException("Utility classes should not be instantiated.");
    }

    /**
     * Get raw type by type.
     * Only for ParametrizedTypes, GenericArrayTypes and Classes.
     *
     * Empty optional is returned if raw type cannot be resolved.
     *
     * @param type Type to get class information from, not null.
     * @return Class of a type.
     */
    public static Optional<Class<?>> getOptionalRawType(Type type) {
        if (type instanceof Class) {
            return Optional.of((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return Optional.of((Class<?>) ((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            return Optional.of(((GenericArrayType) type).getClass());
        }
        return Optional.empty();
    }

    /**
     * Get raw type by type.
     * Resolves only ParametrizedTypes, GenericArrayTypes and Classes.
     *
     * Exception is thrown if raw type cannot be resolved.
     *
     * @param type Type to get class information from, not null.
     * @return Class of a raw type.
     */
    public static Class<?> getRawType(Type type) {
        return getOptionalRawType(type)
                .orElseThrow(() -> new JsonbException(Messages.getMessage(MessageKeys.TYPE_RESOLUTION_ERROR, type)));
    }

    /**
     * Get a raw type of any type.
     * If type is a {@link TypeVariable} recursively search {@link AbstractItem} for resolution of typevar.
     * If type is a {@link WildcardType} find most specific upper / lower bound, which can be used. If most specific
     * bound is a {@link TypeVariable}, perform typevar resolution.
     *
     * @param item item containing wrapper class of a type field, not null.
     * @param type type to resolve, typically field type or generic bound, not null.
     * @return resolved raw class
     */
    public static Class<?> resolveRawType(RuntimeTypeInfo item, Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return getRawType(resolveType(item, type));
        }
    }
    
    /**
     * Resolve a type by item.
     * If type is a {@link TypeVariable} recursively search {@link AbstractItem} for resolution of typevar.
     * If type is a {@link WildcardType} find most specific upper / lower bound, which can be used. If most specific
     * bound is a {@link TypeVariable}, perform typevar resolution.
     *
     * @param item item containing wrapper class of a type field, not null.
     * @param type type to resolve, typically field type or generic bound, not null.
     * @return resolved type
     */
    public static Type resolveType(RuntimeTypeInfo item, Type type) {
        return resolveType(item, type, true);
    }

    private static Type resolveType(RuntimeTypeInfo item, Type type, boolean warn) {
        if (type instanceof WildcardType) {
            return resolveMostSpecificBound(item, (WildcardType) type, warn);
        } else if (type instanceof TypeVariable) {
            return resolveItemVariableType(item, (TypeVariable<?>) type, warn);
        } else if (type instanceof ParameterizedType && item != null) {
            return resolveTypeArguments((ParameterizedType) type, item.getRuntimeType());
        }
        return type;
    }

    /**
     * Resolves type by item information and wraps it with {@link Optional}.
     *
     * @param info item information
     * @param type type
     * @return resolved type wrapped with Optional
     */
    public static Optional<Type> resolveOptionalType(RuntimeTypeInfo info, Type type) {
        try {
            return Optional.of(resolveType(info, type, false));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Resolve a bounded type variable type by its wrapper types.
     * Resolution could be done only if a compile time generic information is provided, either:
     * by generic field or subclass of a generic class.
     *
     * @param whether or not to log a warning message when bounds are not found
     * @param item         item to search "runtime" generic type of a TypeVariable.
     * @param typeVariable type to search in item for, not null.
     * @return Type of a generic "runtime" bound, not null.
     */
    static Type resolveItemVariableType(RuntimeTypeInfo item, TypeVariable<?> typeVariable, boolean warn) {
        if (item == null) {
            //Bound not found, treat it as an Object.class
            if (warn) {
                LOGGER.warning(Messages.getMessage(MessageKeys.GENERIC_BOUND_NOT_FOUND,
                                                   typeVariable,
                                                   typeVariable.getGenericDeclaration()));
            }
            return Object.class;
        }

        //Embedded items doesn't hold information about variable types
        if (item instanceof EmbeddedItem) {
            return resolveItemVariableType(item.getWrapper(), typeVariable, warn);
        }

        ParameterizedType wrapperParameterizedType = findParameterizedSuperclass(item.getRuntimeType());

        VariableTypeInheritanceSearch search = new VariableTypeInheritanceSearch();
        Type foundType = search.searchParametrizedType(wrapperParameterizedType, typeVariable);
        if (foundType != null) {
            if (foundType instanceof TypeVariable) {
                return resolveItemVariableType(item.getWrapper(), (TypeVariable<?>) foundType, warn);
            }
            return foundType;
        }

        return resolveItemVariableType(item.getWrapper(), typeVariable, warn);
    }

    /**
     * Resolves {@link TypeVariable} arguments of generic types.
     *
     * @param typeToResolve type to resolve
     * @param typeToSearch  type to search
     * @return resolved type
     */
    public static Type resolveTypeArguments(ParameterizedType typeToResolve, Type typeToSearch) {
        final Type[] unresolvedArgs = typeToResolve.getActualTypeArguments();
        Type[] resolvedArgs = new Type[unresolvedArgs.length];
        for (int i = 0; i < unresolvedArgs.length; i++) {
            if (!(unresolvedArgs[i] instanceof TypeVariable)) {
                resolvedArgs[i] = unresolvedArgs[i];
            } else {
                resolvedArgs[i] = new VariableTypeInheritanceSearch()
                        .searchParametrizedType(typeToSearch, (TypeVariable<?>) unresolvedArgs[i]);
                if (resolvedArgs[i] == null) {
                    //No generic information available
                    throw new IllegalStateException(Messages.getMessage(MessageKeys.GENERIC_BOUND_NOT_FOUND,
                                                                        unresolvedArgs[i],
                                                                        typeToSearch));
                }
            }
            if (resolvedArgs[i] instanceof ParameterizedType) {
                resolvedArgs[i] = resolveTypeArguments((ParameterizedType) resolvedArgs[i], typeToSearch);
            }
        }
        return Arrays.equals(resolvedArgs, unresolvedArgs)
                ? typeToResolve
                : new ResolvedParameterizedType(typeToResolve, resolvedArgs);
    }

    /**
     * Create instance with constructor.
     *
     * @param constructor const not null
     * @param <T>         type of instance
     * @return instance
     */
    public static <T> T createNoArgConstructorInstance(Constructor<T> constructor) {
        Objects.requireNonNull(constructor);
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonbException("Can't create instance", e);
        }
    }

    /**
     * Get default no argument constructor of the class.
     *
     * @param clazz    Class to get constructor from
     * @param <T>      Class generic type
     * @param required if true, throws an exception if the default constructor is missing.
     *                 If false, returns null in that case
     * @return the constructor of the class, or null. Depending on required.
     */
    public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz, boolean required) {
        Objects.requireNonNull(clazz);
        return AccessController.doPrivileged((PrivilegedAction<Constructor<T>>) () -> {
            try {
                final Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
                if (declaredConstructor.getModifiers() == Modifier.PROTECTED) {
                    declaredConstructor.setAccessible(true);
                }
                return declaredConstructor;
            } catch (NoSuchMethodException e) {
                if (required) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.NO_DEFAULT_CONSTRUCTOR, clazz), e);
                }
                return null;
            }
        });
    }

    /**
     * For generic adapters like:
     * <p>
     * {@code
     * interface ContainerAdapter<T> extends JsonbAdapter<Box<T>, Crate<T>>...;
     * class IntegerBoxToCrateAdapter implements ContainerAdapter<Integer>...;
     * }
     * </p>
     * We need to find a JsonbAdapter class which will hold basic generic type arguments,
     * and resolve them if they are TypeVariables from there.
     *
     * @param classToSearch          class to resolve parameterized interface
     * @param parameterizedInterface interface to search
     * @return type of JsonbAdapter
     */
    public static ParameterizedType findParameterizedType(Class<?> classToSearch, Class<?> parameterizedInterface) {
        Class current = classToSearch;
        while (current != Object.class) {
            for (Type currentInterface : current.getGenericInterfaces()) {
                if (currentInterface instanceof ParameterizedType
                        && parameterizedInterface.isAssignableFrom(
                        ReflectionUtils.getRawType(((ParameterizedType) currentInterface).getRawType()))) {
                    return (ParameterizedType) currentInterface;
                }
            }
            current = current.getSuperclass();
        }
        throw new JsonbException(Messages.getMessage(MessageKeys.NON_PARAMETRIZED_TYPE, parameterizedInterface));
    }

    /**
     * Check if type needs resolution. If type is a class or a parametrized type with all type arguments as classes
     * than it is considered resolved. If any of types is type variable or wildcard type is not resolved.
     *
     * @param type Type to check.
     * @return True if resolved
     */
    public static boolean isResolvedType(Type type) {
        if (type instanceof ParameterizedType) {
            for (Type typeArg : ((ParameterizedType) type).getActualTypeArguments()) {
                if (!isResolvedType(typeArg)) {
                    return false;
                }
            }
            return true;
        }
        return type instanceof Class<?>;
    }

    private static ParameterizedType findParameterizedSuperclass(Type type) {
        if (type == null || type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        if (!(type instanceof Class)) {
            throw new JsonbException("Can't resolve ParameterizedType superclass for: " + type);
        }
        return findParameterizedSuperclass(((Class) type).getGenericSuperclass());
    }

    /**
     * Resolves a wildcard most specific upper or lower bound.
     *
     * @param item         Type.
     * @param wildcardType Wildcard type.
     * @return The most specific type.
     */
    private static Type resolveMostSpecificBound(RuntimeTypeInfo item, WildcardType wildcardType, boolean warn) {
        Class<?> result = Object.class;
        for (Type upperBound : wildcardType.getUpperBounds()) {
            result = getMostSpecificBound(item, result, upperBound, warn);
        }
        for (Type lowerBound : wildcardType.getLowerBounds()) {
            result = getMostSpecificBound(item, result, lowerBound, warn);
        }
        return result;
    }

    private static Class<?> getMostSpecificBound(RuntimeTypeInfo item, Class<?> result, Type bound, boolean warn) {
        if (bound == Object.class) {
            return result;
        }
        //if bound is type variable search recursively for wrapper generic expansion
        Type resolvedBoundType = bound instanceof TypeVariable ? resolveType(item, bound, warn) : bound;
        Class<?> boundRawType = getRawType(resolvedBoundType);
        //resolved class is a subclass of a result candidate
        if (result.isAssignableFrom(boundRawType)) {
            result = boundRawType;
        }
        return result;
    }
}
