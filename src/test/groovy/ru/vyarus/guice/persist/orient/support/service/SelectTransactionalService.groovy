package ru.vyarus.guice.persist.orient.support.service

import com.google.inject.Provider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import ru.vyarus.guice.persist.orient.support.model.Model

import javax.inject.Inject

/**
 * Class level transactional annotation can't be used because of groovy.
 * @author Vyacheslav Rusakov 
 * @since 19.07.2014
 */
@javax.inject.Singleton
class SelectTransactionalService {

    @Inject
    Provider<ODatabaseObject> provider

    @Transactional
    public Model select() {
        // query object inserted by TransactionalService
        final ODatabaseObject db = provider.get()
        final List<Model> list = db.query(new OSQLSynchQuery<Object>("select from Model where name = 'John'"))
        return list.empty ? null : list[0]
    }

    @Transactional
    public void rollbackCheck() {
        throw new IllegalArgumentException("Checking proper rollback")
    }
}
