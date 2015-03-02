package ru.vyarus.guice.persist.orient.repository.delegate.support.amend.ext

import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension
import ru.vyarus.guice.persist.orient.repository.delegate.spi.DelegateMethodDescriptor

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
class DummyAmendExtension implements AmendMethodExtension<DelegateMethodDescriptor, DummyAmend> {

    static String used

    @Override
    void handleAnnotation(DelegateMethodDescriptor descriptor, DummyAmend annotation) {
        used = annotation.value()
    }
}
