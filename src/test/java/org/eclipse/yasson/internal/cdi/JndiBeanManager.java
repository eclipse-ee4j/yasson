/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.cdi;

import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.enterprise.context.spi.Context;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedMember;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedParameter;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanAttributes;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Decorator;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.enterprise.inject.spi.InjectionTargetFactory;
import jakarta.enterprise.inject.spi.InterceptionFactory;
import jakarta.enterprise.inject.spi.InterceptionType;
import jakarta.enterprise.inject.spi.Interceptor;
import jakarta.enterprise.inject.spi.ObserverMethod;
import jakarta.enterprise.inject.spi.ProducerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class JndiBeanManager implements BeanManager {
    @Override
    public Object getReference(Bean<?> bean, Type beanType, CreationalContext<?> ctx) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Object getInjectableReference(InjectionPoint ij, CreationalContext<?> ctx) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> CreationalContext<T> createCreationalContext(Contextual<T> contextual) {
        return null;
    }

    @Override
    public Set<Bean<?>> getBeans(Type beanType, Annotation... qualifiers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<Bean<?>> getBeans(String name) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Bean<?> getPassivationCapableBean(String id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <X> Bean<? extends X> resolve(Set<Bean<? extends X>> beans) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void validate(InjectionPoint injectionPoint) {

    }

    @Override
    public <T> Set<ObserverMethod<? super T>> resolveObserverMethods(T event, Annotation... qualifiers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Decorator<?>> resolveDecorators(Set<Type> types, Annotation... qualifiers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Interceptor<?>> resolveInterceptors(InterceptionType type, Annotation... interceptorBindings) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isScope(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public boolean isNormalScope(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public boolean isQualifier(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public boolean isStereotype(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public Set<Annotation> getInterceptorBindingDefinition(Class<? extends Annotation> bindingType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<Annotation> getStereotypeDefinition(Class<? extends Annotation> stereotype) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean areQualifiersEquivalent(Annotation qualifier1, Annotation qualifier2) {
        return false;
    }

    @Override
    public boolean areInterceptorBindingsEquivalent(Annotation interceptorBinding1, Annotation interceptorBinding2) {
        return false;
    }

    @Override
    public int getQualifierHashCode(Annotation qualifier) {
        return 0;
    }

    @Override
    public int getInterceptorBindingHashCode(Annotation interceptorBinding) {
        return 0;
    }

    @Override
    public Context getContext(Class<? extends Annotation> scopeType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Collection<Context> getContexts(Class<? extends Annotation> aClass) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public ELResolver getELResolver() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public ExpressionFactory wrapExpressionFactory(ExpressionFactory expressionFactory) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> AnnotatedType<T> createAnnotatedType(Class<T> type) {
        return null;
    }

    @Override
    public <T> InjectionTargetFactory<T> getInjectionTargetFactory(AnnotatedType<T> annotatedType) {
        return new MockInjectionTargetFactory<>();
    }

    @Override
    public <X> ProducerFactory<X> getProducerFactory(AnnotatedField<? super X> field, Bean<X> declaringBean) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <X> ProducerFactory<X> getProducerFactory(AnnotatedMethod<? super X> method, Bean<X> declaringBean) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> BeanAttributes<T> createBeanAttributes(AnnotatedType<T> type) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BeanAttributes<?> createBeanAttributes(AnnotatedMember<?> type) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> Bean<T> createBean(BeanAttributes<T> attributes, Class<T> beanClass, InjectionTargetFactory<T> injectionTargetFactory) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T, X> Bean<T> createBean(BeanAttributes<T> attributes, Class<X> beanClass, ProducerFactory<X> producerFactory) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public InjectionPoint createInjectionPoint(AnnotatedField<?> field) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public InjectionPoint createInjectionPoint(AnnotatedParameter<?> parameter) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T extends Extension> T getExtension(Class<T> extensionClass) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> InterceptionFactory<T> createInterceptionFactory(CreationalContext<T> ctx, Class<T> clazz) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Event<Object> getEvent() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Instance<Object> createInstance() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isMatchingBean(Set<Type> set, Set<Annotation> set1, Type type, Set<Annotation> set2) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isMatchingEvent(Type type, Set<Annotation> set, Type type1, Set<Annotation> set1) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
