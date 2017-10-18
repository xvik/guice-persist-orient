package ru.vyarus.guice.persist.orient.repository.command.ext.listen

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.command.OCommandResultListener
import ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 27.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface ListenCases {

    @AsyncQuery("select from Model")
    void select(@Listen OCommandResultListener listener)

    // error: wrong listener type
    @AsyncQuery("select from Model")
    List<Model> wrongType(@Listen Object listener)

    // error: duplicate definition
    @AsyncQuery("select from Model")
    List<Model> duplicate(@Listen OCommandResultListener listener, @Listen OCommandResultListener listener2)

    // error listener applied for update query (in this case listener will simply will not be called, but error indicates improper usage)
    @AsyncQuery("update Model set name=? where name=?")
    void updateWithListener(String replace, String original, @Listen OCommandResultListener listener)

    // error: this will normally work, but will always return empty collection, so explicit error thrown
    @AsyncQuery("select from Model")
    List<Model> returnType(@Listen OCommandResultListener listener)
}
