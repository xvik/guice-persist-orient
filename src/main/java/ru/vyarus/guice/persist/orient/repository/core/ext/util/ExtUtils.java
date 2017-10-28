package ru.vyarus.guice.persist.orient.repository.core.ext.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;
import ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * Utilities to resolve find extension annotations.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod
 * @see ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod
 * @see ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam
 * @since 18.02.2015
 */
public final class ExtUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtUtils.class);

    private static final Function<Annotation, Class> ANNOTATION_CLASS_FUNCTION = new Function<Annotation, Class>() {
        @Nonnull
        @Override
        public Class apply(@Nonnull final Annotation input) {
            return input.annotationType();
        }
    };

    private ExtUtils() {
    }

    /**
     * Searches for method annotation (annotation annotated with
     * {@link ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod}).
     * <p>
     * Annotation may be set on method directly or on method declaring type.
     * <p>
     * Throws error if more than one matching annotation found.
     *
     * @param method repository method
     * @return found annotation or null
     */
    public static Annotation findMethodAnnotation(final Method method) {
        return findSingleExtAnnotation(method, RepositoryMethod.class);
    }


    /**
     * Searches for parameter annotation (annotation annotated with
     * {@link ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam}).
     * <p>
     * Throws error if more than one annotation found
     *
     * @param annotations method parameter annotations
     * @return found annotation or null
     */
    public static Annotation findParameterExtension(final Annotation... annotations) {
        final List<Annotation> anns = filterAnnotations(MethodParam.class, annotations);
        check(anns.size() <= 1, "Parameter may have just one extension annotation of: %s",
                toStringAnnotations(anns));
        Annotation res = null;
        if (!anns.isEmpty()) {
            res = anns.get(0);
        }
        return res;
    }

    /**
     * Searches for amend annotations (annotations annotated with
     * {@link ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod}).
     * <p>
     * Amend annotation may be defined on method, type and probably globally on root repository type.
     * If annotation is defined in two places then only more prioritized will be used.
     * Priorities: method, direct method type, repository type (in simple cases the last two will be the same type).
     * <p>
     * Extensions compatibility is checked against descriptor object. If extension declared directly on method
     * error will be throw (bad usage). For type and root type declared extensions, incompatible extensions simply
     * skipped (case when extension should apply to all methods except few).
     *
     * @param method         repository method
     * @param root           root repository type
     * @param descriptorType type of descriptor object (used to filter extensions)
     * @return list of found extension annotations
     */
    public static List<Annotation> findAmendAnnotations(
            final Method method, final Class<?> root,
            final Class<? extends RepositoryMethodDescriptor> descriptorType) {
        final List<Annotation> res = filterAnnotations(AmendMethod.class, method.getAnnotations());
        // strict check: if bad annotation defined - definitely error
        ExtCompatibilityUtils.checkAmendExtensionsCompatibility(descriptorType, res);
        // some annotations may be defined on type; these annotations are filtered according to descriptor
        merge(res, filterAnnotations(AmendMethod.class, method.getDeclaringClass().getAnnotations()), descriptorType);
        if (root != method.getDeclaringClass()) {
            // global extensions may be applied on repository level
            merge(res, filterAnnotations(AmendMethod.class, root.getAnnotations()), descriptorType);
        }
        return res;
    }

    /**
     * Searches for result converter annotations (annotations annotated with
     * {@link ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter}).
     * <p>
     * Annotation may be defined on method, type and probably globally on root repository type.
     * If annotation is defined in two places then only more prioritized will be used.
     * Priorities: method, direct method type, repository type (in simple cases the last two will be the same type).
     *
     * @param method method to search converter
     * @param root   root descriptor type
     * @return found converter annotation or null
     */
    public static Annotation findResultConverter(final Method method, final Class<?> root) {
        Annotation res = findSingleExtAnnotation(method, ResultConverter.class);
        if (res == null) {
            final List<Annotation> annotations = filterAnnotations(ResultConverter.class, root.getAnnotations());
            check(annotations.size() <= 1, "Root %s must use only one annotation of: %s",
                    root.getName(), toStringAnnotations(annotations));
            if (!annotations.isEmpty()) {
                res = annotations.get(0);
            }
        }
        return res;
    }

    private static Annotation findSingleExtAnnotation(final Method method, final Class<? extends Annotation> type) {
        List<Annotation> annotations = filterAnnotations(type, method.getAnnotations());
        check(annotations.size() <= 1, "Method must use only one annotation of: %s",
                toStringAnnotations(annotations));
        Annotation res = null;
        if (annotations.isEmpty()) {
            // some annotations may be defined on mixin type (e.g. Delegate)
            annotations = filterAnnotations(type, method.getDeclaringClass().getAnnotations());
            check(annotations.size() <= 1, "Type %s must use only one annotation of: %s",
                    method.getDeclaringClass().getName(), toStringAnnotations(annotations));
        }
        if (!annotations.isEmpty()) {
            res = annotations.get(0);
        }
        return res;
    }

    private static List<Annotation> filterAnnotations(final Class<? extends Annotation> type,
                                                      final Annotation... annotations) {
        final List<Annotation> res = Lists.newArrayList();
        for (Annotation ann : annotations) {
            if (ann.annotationType().isAnnotationPresent(type)) {
                res.add(ann);
            }
        }
        return res;
    }

    private static void merge(final List<Annotation> exts, final List<Annotation> additional,
                              final Class<? extends RepositoryMethodDescriptor> descriptorType) {
        final List<Class> types = Lists.transform(exts, ANNOTATION_CLASS_FUNCTION);
        for (Annotation ann : additional) {
            final Class<? extends Annotation> annType = ann.annotationType();
            // avoid duplicates
            if (!types.contains(annType)) {
                // filtering incompatible extensions, because they may be intended to apply to some methods only
                if (ExtCompatibilityUtils.isCompatible(annType.getAnnotation(AmendMethod.class).value(),
                        AmendMethodExtension.class, descriptorType)) {
                    exts.add(ann);
                } else {
                    LOGGER.debug("Amend extension @{} ignored as incompatible with descriptor {}",
                            ann.annotationType().getSimpleName(), descriptorType.getSimpleName());
                }
            }
        }
    }

    private static String toStringAnnotations(final List<Annotation> annotations) {
        final List<Class> types = Lists.transform(annotations, ANNOTATION_CLASS_FUNCTION);
        return Joiner.on(", ").join(types);
    }
}
