package ru.vyarus.guice.persist.orient.repository.core.ext;

import com.google.inject.Injector;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.AmendExtensionsService;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.ParamsService;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ResultService;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtUtils;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ResultUtils;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethod;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethodExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamsContext;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;

/**
 * Main extensions service. Method extensions must use it in order to correctly process parameters, amend and
 * execution extensions.
 *
 * @author Vyacheslav Rusakov
 * @since 18.02.2015
 */
@Singleton
public class SpiService {

    private final Injector injector;
    private final ParamsService paramsService;
    private final AmendExtensionsService amendExtensionsService;
    private final ResultService resultService;

    @Inject
    public SpiService(final Injector injector,
                      final ParamsService paramsService,
                      final AmendExtensionsService amendExtensionsService,
                      final ResultService resultService) {
        this.injector = injector;
        this.paramsService = paramsService;
        this.amendExtensionsService = amendExtensionsService;
        this.resultService = resultService;
    }

    /**
     * Finds method extension annotation and resolve extension instance from it.
     * Implicitly updates incoming context with extension type (other extensions could use it).
     * Updates descriptor, created by extension with extension provider.
     *
     * @param context repository method context
     * @return descriptor object created by extension
     */
    public RepositoryMethodDescriptor createMethodDescriptor(final DescriptorContext context) {
        final Annotation annotation = ExtUtils.findMethodAnnotation(context.method);
        context.extensionAnnotation = annotation.annotationType();
        context.extensionType = annotation.annotationType().getAnnotation(RepositoryMethod.class).value();
        final Provider<? extends RepositoryMethodExtension> extension = injector.getProvider(context.extensionType);
        @SuppressWarnings("unchecked")
        final RepositoryMethodDescriptor<?> descriptor = extension.get().createDescriptor(context, annotation);
        descriptor.methodExtension = extension;
        return descriptor;
    }


    /**
     * Parse method parameters. Resolves and applies all found parameter extensions.
     * Resolves execution extensions and store ordered list of all found extensions in descriptor
     * (extension will use them directly). Resolves result conversion extensions.
     * <p>
     * Called by method extension directly.
     *
     * @param descriptor    repository method descriptor
     * @param paramsContext extension specific parameters context
     */
    public void process(final RepositoryMethodDescriptor descriptor, final ParamsContext paramsContext) {
        paramsService.processParams(descriptor, paramsContext);
        // parameter extensions may also be amend extensions
        amendExtensionsService.registerExtensions(descriptor, paramsContext);
        resultService.registerExtensions(descriptor, paramsContext.getExtensionsContext());
    }

    /**
     * @param descriptor repository method descriptor
     * @param result     result object
     * @return converted result
     */
    @SuppressWarnings("unchecked")
    public Object convert(final RepositoryMethodDescriptor descriptor, final Object result) {
        final Object res = resultService.convert(descriptor, result);
        ResultUtils.check(res, descriptor.result.expectType);
        return res;
    }
}
