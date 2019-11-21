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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Search for type variable in inheritance hierarchy and resolve if possible.
 */
public class VariableTypeInheritanceSearch {

    private final Deque<ParameterizedType> parameterizedSubclasses = new ArrayDeque<>();

    /**
     * Searches the hierarchy of classes to resolve a type variable. If typevar resolved value is another typevar redirection
     * (propagated from wrapping class),
     * this typevar is returned.
     *
     * <pre>
     *
     * Example 1: typevar is resolved
     *
     *
     * class GenericClass &lt;T&gt; {
     *     private T genericField;
     * }
     * class ConcreteClass extends GenericClass&lt;MyPojo&gt; {
     *     //...
     * }
     *
     * In above case when ConcreteClass type is passed as runtime type and &lt;T&gt; as type variable, T is resolved to MyPojo.
     * </pre>
     *
     *
     * <pre>
     * Example 2: typevar is resolved to another propagated typevar
     *
     *
     * class WrapperGenericClass&lt;X&gt; {
     *     private GenericClass&lt;X&gt; propagatedGenericField
     * }
     *
     * class AnotherClass extends WrapperGenericClass&lt;MyPojo&gt; {
     * }
     *
     *
     * In second case when GenericClass {@link ParameterizedType} is passed as runtime type and &lt;T&gt; as type variable,
     * T is resolved to propagated &lt;X&gt; by WrapperGenericClass.
     *
     * Resolution on &lt;X&gt; must be performed thereafter with AnotherClass runtime type.
     * </pre>
     *
     * @param typeToSearch runtime type to search for typevar in, not null
     * @param typeVar      type variable to resolve, not null
     * @return resolved runtime type, or type variable
     */
    public Type searchParametrizedType(Type typeToSearch, TypeVariable<?> typeVar) {
        ParameterizedType parameterizedType = findParameterizedSuperclass(typeToSearch);
        if (parameterizedType == null) {
            return null;
        }
        Type matchedGenericType = searchRuntimeTypeArgument(parameterizedType, typeVar);
        if (matchedGenericType != null) {
            return matchedGenericType;
        }
        parameterizedSubclasses.push(parameterizedType);
        return searchParametrizedType(((Class) parameterizedType.getRawType()).getGenericSuperclass(), typeVar);
    }

    private Type checkSubclassRuntimeInfo(TypeVariable typeVar) {
        if (parameterizedSubclasses.size() == 0) {
            return typeVar;
        }
        ParameterizedType parametrizedSubclass = parameterizedSubclasses.pop();
        return searchRuntimeTypeArgument(parametrizedSubclass, typeVar);
    }

    private Type searchRuntimeTypeArgument(ParameterizedType runtimeType, TypeVariable<?> typeVar) {
        if (ReflectionUtils.getRawType(runtimeType) != typeVar.getGenericDeclaration()) {
            return null;
        }
        TypeVariable[] bounds = typeVar.getGenericDeclaration().getTypeParameters();
        for (int i = 0; i < bounds.length; i++) {
            if (bounds[i].equals(typeVar)) {
                Type matchedGenericType = runtimeType.getActualTypeArguments()[i];
                //Propagated generic types to another generic classes
                if (matchedGenericType instanceof TypeVariable<?>) {
                    return checkSubclassRuntimeInfo((TypeVariable) matchedGenericType);
                }
                //found runtime matchedGenericType
                return matchedGenericType;
            }
        }
        return null;
    }

    private static ParameterizedType findParameterizedSuperclass(Type type) {
        if (type == null || type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        if (!(type instanceof Class)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.RESOLVE_PARAMETRIZED_TYPE, type));
        }
        return findParameterizedSuperclass(((Class) type).getGenericSuperclass());
    }
}
