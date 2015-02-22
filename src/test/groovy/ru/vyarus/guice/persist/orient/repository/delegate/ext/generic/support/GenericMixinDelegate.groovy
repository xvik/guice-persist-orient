package ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic
import ru.vyarus.guice.persist.orient.support.model.Model

import java.util.concurrent.Callable

/**
 * Using abstract class to tie delegate to interface (even if no methods are directly implemented)
 *
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@ProvidedBy(DynamicSingletonProvider)
abstract class GenericMixinDelegate implements GenericMixin {

    List getAll(@Generic("T") Class<Model> type) {
        [new Model(name: 'getAll')]
    }

    // generic lookup on specific class
    List getAll2(@Generic(value = "P", genericHolder = OtherMixin) Class<Model> type) {
        [new Model(name: 'getAll2')]
    }

    // duplicate generic is allowed - too obvious error
    List duplicateGeneric(@Generic("T") Class<Model> type, @Generic("T") Class<Model> type2) {
        [new Model(name: 'duplicateGeneric')]
    }

    // error: lookup generic on type, not present in repository hierarchy
    List lookupError(@Generic(value = "P", genericHolder = Callable) Class<Model> type) {
        [new Model(name: 'tst')]
    }

    // error: bad generic name
    List genericError(@Generic("E") Class<Model> type) {
    }

    // error: incompatible generic type
    List genericTypeError(@Generic("T") Object type) {
    }
}
