package ru.vyarus.guice.persist.orient.repository.core.ext.support.exts

import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateExtension
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class DelegateAmendExtension implements AmendMethodExtension<DelegateMethodDescriptor, DelegateAmend>,
        DelegateExtension<DelegateMethodDescriptor> {

    @Override
    void handleAnnotation(DelegateMethodDescriptor descriptor, DelegateAmend annotation) {

    }

    @Override
    void amendParameters(DelegateMethodDescriptor descriptor, Object[] targetArgs, Object instance, Object... arguments) {

    }
}
