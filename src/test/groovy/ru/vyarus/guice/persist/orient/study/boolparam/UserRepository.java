package ru.vyarus.guice.persist.orient.study.boolparam

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud

/**
 * @author Vyacheslav Rusakov 
 * @since 27.05.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface UserRepository extends ObjectCrud<User> {

    @Query("select from User where username = ? and active = ? limit 1")
    User findActiveByUsername(String username, boolean active);
}