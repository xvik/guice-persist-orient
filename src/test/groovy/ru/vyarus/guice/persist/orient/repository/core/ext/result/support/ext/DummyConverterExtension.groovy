package ru.vyarus.guice.persist.orient.repository.core.ext.result.support.ext

import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultExtension

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
class DummyConverterExtension implements ResultExtension<DummyConverter> {

    @Override
    void handleAnnotation(RepositoryMethodDescriptor descriptor, DummyConverter annotation) {
    }

    @Override
    Object convert(RepositoryMethodDescriptor descriptor, Object result) {
        return null
    }
}
