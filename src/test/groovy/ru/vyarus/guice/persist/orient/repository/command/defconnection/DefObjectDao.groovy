package ru.vyarus.guice.persist.orient.repository.command.defconnection

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * @author Vyacheslav Rusakov 
 * @since 13.02.2015
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
interface DefObjectDao {

    // no generic - document db will be selected
    @Query("select from Model")
    List selectAllNoType()
}
