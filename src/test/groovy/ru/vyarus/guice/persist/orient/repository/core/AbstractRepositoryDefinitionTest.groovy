package ru.vyarus.guice.persist.orient.repository.core

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.core.MethodDescriptorFactory
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor

import java.lang.reflect.Method

/**
 * @author Vyacheslav Rusakov 
 * @since 17.10.2014
 */
abstract class AbstractRepositoryDefinitionTest extends AbstractTest {

    @Inject
    MethodDescriptorFactory factory

    RepositoryMethodDescriptor lookup(Method method) {
        lookup(method.getDeclaringClass(), method)
    }

    RepositoryMethodDescriptor lookup(Class iface, Method method) {
        context.doInTransaction({
            return factory.create(method, iface)
        } as SpecificTxAction)
    }
}