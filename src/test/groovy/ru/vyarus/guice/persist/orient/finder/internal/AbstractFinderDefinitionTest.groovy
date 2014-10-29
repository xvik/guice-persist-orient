package ru.vyarus.guice.persist.orient.finder.internal

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.finder.internal.generics.FinderGenericsFactory
import ru.vyarus.guice.persist.orient.finder.internal.generics.GenericsDescriptor

import java.lang.reflect.Method

/**
 * @author Vyacheslav Rusakov 
 * @since 17.10.2014
 */
abstract class AbstractFinderDefinitionTest extends AbstractTest {

    @Inject
    FinderDescriptorFactory factory
    @Inject
    FinderGenericsFactory genericsFactory

    FinderDescriptor lookup(Method method) {
        lookup(method.getDeclaringClass(), method)
    }

    FinderDescriptor lookup(Class iface, Method method) {
        GenericsDescriptor generics = genericsFactory.create(iface);
        template.doInTransaction({
            return factory.create(method, generics)
        } as SpecificTxAction)
    }
}