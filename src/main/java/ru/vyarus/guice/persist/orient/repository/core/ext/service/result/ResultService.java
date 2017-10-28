package ru.vyarus.guice.persist.orient.repository.core.ext.service.result;

import com.google.inject.Injector;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtUtils;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;

/**
 * Service searches for result extensions and performs result conversion according to extensions.
 * <p>
 * Extension compatibility is checked using descriptor type from generic. Error is thrown if method extension is
 * incompatible. If extension defined on type and its incompatible, its just skipped.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter
 * @since 02.03.2015
 */
@Singleton
public class ResultService {

    private final ResultConverter converter;
    private final Injector injector;

    @Inject
    public ResultService(final ResultConverter converter, final Injector injector) {
        this.converter = converter;
        this.injector = injector;
    }

    @SuppressWarnings("unchecked")
    public void registerExtensions(final RepositoryMethodDescriptor descriptor,
                                   final DescriptorContext context) {
        final Annotation ext = ExtUtils.findResultConverter(context.method, context.type);
        final ResultConversionDescriptor desc = new ResultConversionDescriptor();
        desc.useDefaultConverter = true;
        if (ext != null) {
            final ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter ann = ext.annotationType()
                    .getAnnotation(ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultConverter.class);
            desc.useDefaultConverter = ann.applyDefaultConverter();
            desc.customConverter = injector.getProvider(ann.value());
            desc.customConverter.get().handleAnnotation(descriptor, ext);
        }
        descriptor.resultConversion = desc;
    }

    @SuppressWarnings("unchecked")
    public Object convert(final RepositoryMethodDescriptor descriptor, final Object result) {
        Object res = result;
        final ResultConversionDescriptor conversionInfo = descriptor.resultConversion;
        if (conversionInfo.useDefaultConverter) {
            res = converter.convert(descriptor.result, res);
        }
        if (conversionInfo.customConverter != null) {
            res = conversionInfo.customConverter.get().convert(descriptor, res);
        }
        return res;
    }
}
