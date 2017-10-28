package ru.vyarus.guice.persist.orient.repository.core.ext.service;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtCompatibilityUtils;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtUtils;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamsContext;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for parameters processing. Specific
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamsContext} implementation is responsible
 * for parameters handling and descriptor update.
 * <p>
 * Service itself simply parse parameters, founds extensions, set ordinal parameters (without extensions) into
 * params context and executing extensions on annotated parameters.
 * <p>
 * All found extensions are stored in context for processing by {@link AmendExtensionsService}.
 *
 * @author Vyacheslav Rusakov
 * @since 03.02.2015
 */
@Singleton
public class ParamsService {

    private final Injector injector;

    @Inject
    public ParamsService(final Injector injector) {
        this.injector = injector;
    }

    @SuppressWarnings("unchecked")
    public void processParams(final RepositoryMethodDescriptor descriptor,
                              final ParamsContext paramsContext) {
        final Method method = paramsContext.getDescriptorContext().method;
        final Class<?>[] types = method.getParameterTypes();
        final Annotation[][] annotations = method.getParameterAnnotations();
        final ProcessingContext context = new ProcessingContext();
        for (int i = 0; i < types.length; i++) {
            try {
                processParameter(context, descriptor.getClass(), i, types[i], annotations[i]);
            } catch (Exception ex) {
                // it may seem redundant, but, for example, in case of delegate this will be important
                throw new IllegalStateException(String.format("Error processing parameter %s on method %s",
                        i, RepositoryUtils.methodToString(method)), ex);
            }
        }
        paramsContext.setOrdinals(context.ordinal);
        processExtensions(descriptor, paramsContext, context);
        paramsContext.process(descriptor);
    }

    @SuppressWarnings("unchecked")
    private void processParameter(final ProcessingContext context,
                                  final Class<? extends RepositoryMethodDescriptor> descriptorType,
                                  final int pos, final Class<?> type,
                                  final Annotation... annotations) {
        final Annotation ext = ExtUtils.findParameterExtension(annotations);
        if (ext != null) {
            final Class<? extends MethodParamExtension> extension = ext.annotationType()
                    .getAnnotation(MethodParam.class).value();
            ExtCompatibilityUtils.checkParamExtensionCompatibility(descriptorType, extension);
            if (!context.extensionMap.containsKey(extension)) {
                final MethodParamExtension instance = injector.getInstance(extension);
                context.extensionMap.put(extension, instance);
            }
            context.extParams.put(extension, new ParamInfo(ext, pos, type));
        } else {
            context.ordinal.add(new ParamInfo(pos, type));
        }
    }

    @SuppressWarnings("unchecked")
    private void processExtensions(final RepositoryMethodDescriptor descriptor, final ParamsContext paramsContext,
                                   final ProcessingContext context) {
        for (Map.Entry<Class<? extends MethodParamExtension>, MethodParamExtension> entry
                : context.extensionMap.entrySet()) {
            try {
                entry.getValue().processParameters(descriptor, paramsContext, Lists.newArrayList(
                        context.extParams.get(entry.getKey())));
            } catch (Throwable th) {
                final DescriptorContext descriptorContext = paramsContext.getDescriptorContext();
                // it may seem redundant, but, for example, in case of delegate this will be important
                throw new IllegalStateException(
                        String.format("Error processing %s parameter extension on method %s",
                                entry.getValue().getClass().getSimpleName(),
                                RepositoryUtils.methodToString(descriptorContext.type,
                                        descriptorContext.method)), th);
            }
        }
        paramsContext.setExtensions(Lists.newArrayList(context.extensionMap.values()));
    }

    /**
     * Internal parameters processing context.
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    private static class ProcessingContext {
        public Map<Class<? extends MethodParamExtension>, MethodParamExtension> extensionMap =
                Maps.newLinkedHashMap();
        public Multimap<Class<? extends MethodParamExtension>, ParamInfo> extParams = LinkedHashMultimap.create();
        public List<ParamInfo> ordinal = Lists.newArrayList();
    }
}
