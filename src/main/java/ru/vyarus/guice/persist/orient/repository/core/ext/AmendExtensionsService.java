package ru.vyarus.guice.persist.orient.repository.core.ext;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamsContext;
import ru.vyarus.guice.persist.orient.repository.core.util.OrderComparator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Resolves and applies amend extensions. Analyze found params extensions (provided in params context)
 * and compose all found extensions in descriptor. Composed extensions must be used directly
 * by method extensions (because contracts are very different for different repository method types).
 * <p>Resolved extensions are sorted using {@link ru.vyarus.guice.persist.orient.repository.core.util.Order}
 * annotations.</p>
 * <p>Extension compatibility is strictly checked for extensions defined on method. Type level extension
 * are simply filtered if they are incompatible with current descriptor (extensions has required descriptor
 * in generic). Finally, resolved list of execution extensions are filtered according to descriptor
 * generic value (supported specific extension).</p>
 *
 * @author Vyacheslav Rusakov
 * @since 07.02.2015
 */
@Singleton
public class AmendExtensionsService {

    private final Injector injector;

    @Inject
    public AmendExtensionsService(final Injector injector) {
        this.injector = injector;
    }

    @SuppressWarnings("unchecked")
    public void registerExtensions(final RepositoryMethodDescriptor descriptor,
                                   final ParamsContext paramsContext) {
        final Method method = paramsContext.getDescriptorContext().method;
        final List<AmendExecutionExtension> extensions = Lists.newArrayList();
        // incompatible extensions will be silently filtered (if defined on type)
        final List<Annotation> amendAnnotations = ExtUtils
                .findAmendAnnotations(method, paramsContext.getDescriptorContext().type, descriptor.getClass());
        for (Annotation ann : amendAnnotations) {
            final AmendExecutionExtension ext = processExtension(ann, descriptor);
            if (ext != null) {
                extensions.add(ext);
            }
        }
        extensions.addAll(getParameterExtensions(paramsContext));
        // descriptor has specific extension type in generic, so we could filter list and be 100% sure
        // this is important for universal amend extensions, which may support only some methods
        final List<AmendExecutionExtension> res = ExtCompatibilityUtils
                .filterCompatibleExtensions(extensions, descriptor.getClass());
        Collections.sort(res, OrderComparator.INSTANCE);
        descriptor.amendExtensions = res;
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
