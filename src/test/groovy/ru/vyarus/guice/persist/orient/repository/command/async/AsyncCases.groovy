package ru.vyarus.guice.persist.orient.repository.command.async

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.command.OCommandResultListener
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen
import ru.vyarus.guice.persist.orient.support.model.Model

import java.util.concurrent.Future

/**
 * @author Vyacheslav Rusakov 
 * @since 28.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface AsyncCases {

    @AsyncQuery("select from Model")
    void select(@Listen OCommandResultListener listener)

    // non blocking
    @AsyncQuery(value = "select from Model", blocking = false)
    void selectNonBlocking(@Listen OCommandResultListener listener)

    // non blocking
    @AsyncQuery(value = "select from Model", blocking = false)
    Future selectNonBlockingFuture(@Listen OCommandResultListener listener)

    // error: listener required
    @AsyncQuery("select from Model")
    void noListener()

    // error: void method required (caught by listen extension)
    @AsyncQuery("select from Model")
    List<Model> notVoid(@Listen OCommandResultListener listener)

    // error: void method required (caught by listen extension)
    @AsyncQuery(value = "select from Model", blocking = false)
    List<Model> notVoidNonBlocking(@Listen OCommandResultListener listener)

    // error: update query used for async (caught by listen extension)
    @AsyncQuery("update Model set name = 'name1'")
    void notSelect(@Listen OCommandResultListener listener)
}