/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson.internal.cdi;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanAttributes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InjectionTargetFactory;
import javax.enterprise.inject.spi.InterceptionFactory;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProducerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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
    public void fireEvent(Object event, Annotation... qualifiers) {

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
    @SuppressWarnings("unchecked")
    public <T> InjectionTarget<T> createInjectionTarget(AnnotatedType<T> type) {
        return (InjectionTarget<T>) new MockInjectionTarget();
    }

    @Override
    public <T> InjectionTargetFactory<T> getInjectionTargetFactory(AnnotatedType<T> annotatedType) {
        throw new UnsupportedOperationException("Not implemented");
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
}
