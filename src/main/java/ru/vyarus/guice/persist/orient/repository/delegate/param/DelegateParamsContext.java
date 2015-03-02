package ru.vyarus.guice.persist.orient.repository.delegate.param;

import com.google.common.collect.Lists;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamsContext;
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor;

/**
 * Delegate method parameters context. Note that context is build around target method and not around original
 * repository method (but repository method context is still available).
 *
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
public class DelegateParamsContext extends ParamsContext<DelegateMethodDescriptor> {

    private final DescriptorContext callerContext;

    public DelegateParamsContext(final DescriptorContext delegateContext, final DescriptorContext callerContext) {
        super(delegateContext);
        this.callerContext = callerContext;
    }

    /**
     * @return repository method context
     */
    public DescriptorContext getCallerContext() {
        return callerContext;
    }

    @Override
    public void process(final DelegateMethodDescriptor descriptor) {
        final DelegateParamsDescriptor params = new DelegateParamsDescriptor();
        params.ordinalParams = Lists.transform(getOrdinals(), PARAM_INDEX_FUNCTION);
        descriptor.params = params;
    }

    @Override
    public DescriptorContext getExtensionsContext() {
        // extensions must be searched on calling repository and not on delegate bean
        return getCallerContext();
    }
}
