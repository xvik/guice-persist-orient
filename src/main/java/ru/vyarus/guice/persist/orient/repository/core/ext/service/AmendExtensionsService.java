package ru.vyarus.guice.persist.orient.repository.core.ext.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtCompatibilityUtils;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtUtils;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamsContext;
import ru.vyarus.guice.persist.orient.db.util.OrderComparator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Resolves and applies amend extensions. Analyze found params extensions (provided in params context)
 * and compose all found extensions in descriptor. Composed extensions must be used directly
 * by method extensions (because contracts are very different for different repository method types).
 * <p>
 * Resolved extensions are sorted using {@link ru.vyarus.guice.persist.orient.db.util.Order}
 * annotations.
 * <p>
 * Extension compatibility is strictly checked for extensions defined on method. Type level extension
 * are simply filtered if they are incompatible with current descriptor (extensions has required descriptor
 * in generic). Finally, resolved list of execution extensions are filtered according to descriptor
 * generic value (supported specific extension).
 * <p>
 * Global extension could be registered to apply to every called method. Don't forget that extension are
 * resolved in time of method descriptor creation (first method call). So if you register extension after
 * descriptor composed, it would not be added to existing descriptor. The same with remove: when
 * extension removed, already created descriptors will keep using it. Cache may be cleared to resolve
 * such situations (but generally this should be quite rare case).
 *
 * @author Vyacheslav Rusakov
 * @since 07.02.2015
 */
@Singleton
public class AmendExtensionsService {

    private final Injector injector;
    private final Set<AmendExecutionExtension> globalExtensions = Sets.newHashSet();

    @Inject
    public AmendExtensionsService(final Injector injector) {
        this.injector = injector;
    }

    @SuppressWarnings("unchecked")
    public void registerExtensions(final RepositoryMethodDescriptor descriptor,
                                   final ParamsContext paramsContext) {
        final DescriptorContext context = paramsContext.getExtensionsContext();
        final Method method = context.method;
        final List<AmendExecutionExtension> extensions = Lists.newArrayList();
        // incompatible extensions will be silently filtered (if defined on type)
        final List<Annotation> amendAnnotations = ExtUtils
                .findAmendAnnotations(method, context.type, descriptor.getClass());
        for (Annotation ann : amendAnnotations) {
            final AmendExecutionExtension ext = processExtension(ann, descriptor);
            if (ext != null) {
                extensions.add(ext);
            }
        }
        extensions.addAll(getParameterExtensions(paramsContext));
        extensions.addAll(globalExtensions);
        // descriptor has specific extension type in generic, so we could filter list and be 100% sure
        // this is important for universal amend extensions, which may support only some methods
        final List<AmendExecutionExtension> res = ExtCompatibilityUtils
                .filterCompatibleExtensions(extensions, descriptor.getClass());
        Collections.sort(res, OrderComparator.INSTANCE);
        descriptor.amendExtensions = res;
    }

    /**
     * Register global extension (will be applied to all repository method calls).
     * NOTE: If some descriptors where already created at this moment, they will not be affected
     * with global extension. Be sure to register extension before repository method calls, or
     * flush descriptors cache manually using
     * {@link ru.vyarus.guice.persist.orient.repository.core.MethodDescriptorFactory#clearCache()}.
     *
     * @param ext extension instance
     */
    public void addGlobalExtension(final AmendExecutionExtension ext) {
        globalExtensions.add(ext);
    }

    /**
     * Removes global extension.
     * NOTE: descriptors, created while extension was registered still continue to call it.
     * To avoid it manually flush descriptors cache using
     * {@link ru.vyarus.guice.persist.orient.repository.core.MethodDescriptorFactory#clearCache()}.
     *
     * @param ext extension instance
     */
    public void removeGlobalExtension(final AmendExecutionExtension ext) {
        globalExtensions.remove(ext);
    }

    @SuppressWarnings("unchecked")
    private AmendExecutionExtension processExtension(final Annotation annotation,
                                                     final RepositoryMethodDescriptor descriptor) {
        final Class<? extends AmendMethodExtension> extensionType = annotation.annotationType()
                .getAnnotation(AmendMethod.class).value();
        final AmendMethodExtension extension = injector.getInstance(extensionType);
        extension.handleAnnotation(descriptor, annotation);
        AmendExecutionExtension res = null;
        if (extension instanceof AmendExecutionExtension) {
            res = (AmendExecutionExtension) extension;
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private List<AmendExecutionExtension> getParameterExtensions(final ParamsContext paramsContext) {
        final List<AmendExecutionExtension> extensions = Lists.newArrayList();
        for (MethodParamExtension ext : (List<MethodParamExtension>) paramsContext.getExtensions()) {
            if (ext instanceof AmendExecutionExtension) {
                extensions.add((AmendExecutionExtension) ext);
            }
        }
        return extensions;
    }
}
