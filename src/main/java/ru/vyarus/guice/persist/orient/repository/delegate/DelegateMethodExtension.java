package ru.vyarus.guice.persist.orient.repository.delegate;

import com.google.common.base.Strings;
import com.google.inject.Injector;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethodExtension;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;
import ru.vyarus.guice.persist.orient.repository.delegate.method.TargetMethodAnalyzer;
import ru.vyarus.guice.persist.orient.repository.delegate.param.DelegateParamsContext;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateExtension;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor;
import ru.vyarus.java.generics.resolver.GenericsResolver;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link Delegate} repository method extension.
 *
 * @author Vyacheslav Rusakov
 * @since 02.02.2015
 */
@Singleton
public class DelegateMethodExtension implements RepositoryMethodExtension<DelegateMethodDescriptor, Delegate> {

    private final Injector injector;
    private final SpiService spiService;

    @Inject
    public DelegateMethodExtension(final SpiService spiService, final Injector injector) {
        this.spiService = spiService;
        this.injector = injector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DelegateMethodDescriptor createDescriptor(final DescriptorContext context,
                                                     final Delegate annotation) {
        final DelegateMethodDescriptor descriptor = new DelegateMethodDescriptor();
        final Class<?> delegate = annotation.value();
        descriptor.returnCollectionHint = annotation.returnAs();
        descriptor.connectionHint = annotation.connection();
        descriptor.target = delegate;
        descriptor.method = TargetMethodAnalyzer.findDelegateMethod(context, delegate,
                Strings.emptyToNull(annotation.method()));
        descriptor.instanceProvider = injector.getProvider(delegate);
        processParameters(descriptor, context);
        return descriptor;
    }

    @Override
    public Object execute(final DelegateMethodDescriptor descriptor, final Object repositoryInstance,
                          final Object... arguments) throws Throwable {
        final Object[] args;
        try {
            args = prepareArguments(descriptor, arguments);
            amendParameters(args, descriptor, repositoryInstance, arguments);
        } catch (Exception ex) {
            throw new DelegateMethodException(String.format(
                    "Failed to prepare arguments for calling delegate method %s",
                    RepositoryUtils.methodToString(descriptor.target, descriptor.method)), ex);
        }
        try {
            final Object instance = descriptor.instanceProvider.get();
            return descriptor.method.invoke(instance, args);
        } catch (Throwable th) {
            throw new DelegateMethodException(String.format(
                    "Failed to invoke delegate method %s",
                    RepositoryUtils.methodToString(descriptor.target, descriptor.method)), th);
        }
    }

    private void processParameters(final DelegateMethodDescriptor descriptor, final DescriptorContext context) {
        final DescriptorContext targetContext = new DescriptorContext();
        targetContext.type = descriptor.target;
        targetContext.method = descriptor.method;
        targetContext.generics = GenericsResolver.resolve(targetContext.type)
                .type(descriptor.method.getDeclaringClass());
        targetContext.extensionAnnotation = context.extensionAnnotation;
        targetContext.extensionType = context.extensionType;
        final DelegateParamsContext paramContext = new DelegateParamsContext(targetContext, context);
        spiService.process(descriptor, paramContext);
    }

    private Object[] prepareArguments(final DelegateMethodDescriptor descriptor, final Object... arguments) {
        final int size = descriptor.method.getParameterTypes().length;
        final Object[] res = new Object[size];
        int i = 0;
        for (Integer pos : descriptor.params.ordinalParams) {
            res[pos] = arguments[i++];
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private void amendParameters(final Object[] args, final DelegateMethodDescriptor descriptor,
                                 final Object instance, final Object... arguments) {
        for (DelegateExtension ext : descriptor.amendExtensions) {
            ext.amendParameters(descriptor, args, instance, arguments);
        }
    }
}
