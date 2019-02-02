package org.eclipse.yasson.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Finds an annotation including inherited annotations (e.g. meta-annotations).
 * 
 * @author JohT
 */
class AnnotationFinder<T extends Annotation> {

    private final Class<T> annotationClass;

    /**
     * Gets the {@link AnnotationFinder} for the given Annotation-Type.
     * 
     * @param annotation {@link Class}, that is a sub-type of {@link Annotation}
     * @return {@link AnnotationFinder}
     */
    public static final <S extends Annotation> AnnotationFinder<S> findAnnotation(Class<S> annotation) {
	return new AnnotationFinder<>(annotation);
    }

    /**
     * Only for testing purposes. <br>
     * Please use a static factory method e.g. {@link #findAnnotation(Class)}.
     * 
     * @param annotation {@link Class}
     */
    protected AnnotationFinder(Class<T> annotation) {
	this.annotationClass = annotation;
    }

    public T in(Annotation[] annotations) {
	return findAnnotation(annotations, annotationClass, new HashSet<>());
    }

    /**
     * Searches for annotation, collects processed, to avoid StackOverflow.
     */
    // This method is a copy of "AnnotationIntrospector.findAnnotation"
    @SuppressWarnings("unchecked")
    private T findAnnotation(Annotation[] declaredAnnotations, Class<T> annotationClass, Set<Annotation> processed) {
        for (Annotation candidate : declaredAnnotations) {
            final Class<? extends Annotation> annType = candidate.annotationType();
            if (annType.equals(annotationClass)) {
                return (T) candidate;
            }
            processed.add(candidate);
            final List<Annotation> inheritedAnnotations = new ArrayList<>(Arrays.asList(annType.getDeclaredAnnotations()));
            inheritedAnnotations.removeAll(processed);
            if (inheritedAnnotations.size() > 0) {
                final T inherited = findAnnotation(inheritedAnnotations.toArray(new Annotation[inheritedAnnotations.size()]), annotationClass, processed);
                if (inherited != null) {
                    return inherited;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
	return "AnnotationFinder [annotationClass=" + annotationClass + "]";
    }
}