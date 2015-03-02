package ru.vyarus.guice.persist.orient.repository.core.spi.parameter;

import com.google.common.base.Function;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Base class for extension specific parameters context.
 * Context used to aggregate parameters info during method analysis and after all to update main descriptor.
 *
 * @param <T> descriptor type
 * @author Vyacheslav Rusakov
 * @since 04.02.2015
 */
public abstract class ParamsContext<T extends RepositoryMethodDescriptor> {
    protected static final Function<ParamInfo, Integer> PARAM_INDEX_FUNCTION = new Function<ParamInfo, Integer>() {
        @Nonnull
        @Override
        public Integer apply(@Nonnull final ParamInfo input) {
            return input.position;
        }
    };

    private final DescriptorContext descriptorContext;
    private List<ParamInfo> ordinals;
    private List<MethodParamExtension> extensions;

    public ParamsContext(final DescriptorContext descriptorContext) {
        this.descriptorContext = descriptorContext;
    }

    /**
     * Ordinal parameters are method parameters without any extension annotation.
     * Called by {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.ParamsService}
     * after parameters analysis.
     *
     * @param ordinals ordinal parameters
     */
    public void setOrdinals(final List<ParamInfo> ordinals) {
        this.ordinals = ordinals;
    }

    /**
     * Called by {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.ParamsService}
     * after parameters analysis.
     *
     * @param extensions parameter extensions
     */
    public void setExtensions(final List<MethodParamExtension> extensions) {
        this.extensions = extensions;
    }

    /**
     * @return analyzed method context
     */
    public DescriptorContext getDescriptorContext() {
        return descriptorContext;
    }

    /**
     * Value set only after parameters processing. Intended to be used only in
     * {@link #process(ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor)} method.
     *
     * @return list of ordinal parameters (not affected with param extension)
     */
    public List<ParamInfo> getOrdinals() {
        return ordinals;
    }

    /**
     * Value set only after parameters processing and used in
     * {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.AmendExtensionsService} to compose complete
     * extensions list.
     *
     * @return list of resolved parameter extensions
     */
    public List<MethodParamExtension> getExtensions() {
        return extensions;
    }

    /**
     * Usually extensions defined on repository type and descriptorContext should be used for extensions.
     * But, for example, in case of delegate, descriptorContext is target delegate method context.
     * Its ok for method analysis, but extensions should still work from repository. So for such contexts,
     * this method must be overridden to provide correct context.
     *
     * @return context to use for amend and result extensions resolution.
     */
    public DescriptorContext getExtensionsContext() {
        return getDescriptorContext();
    }

    /**
     * Called after parameters processing to update method descriptor with parameters info.
     *
     * @param descriptor repository method descriptor
     */
    public abstract void process(T descriptor);
}
