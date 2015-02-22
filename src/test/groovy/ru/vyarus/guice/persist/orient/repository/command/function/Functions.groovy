package ru.vyarus.guice.persist.orient.repository.command.function

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface Functions {

    // recognize as function call
    @Function("function1")
    List<Model> function();
}
