package ru.vyarus.guice.persist.orient.support.service

import com.google.inject.Provider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import ru.vyarus.guice.persist.orient.support.model.Model

import javax.inject.Inject

/**
 * Class level transactional annotation can't be used because of groovy.
 * @author Vyacheslav Rusakov 
 * @since 19.07.2014
 */
@javax.inject.Singleton
class InsertTransactionalService {

    @Inject
    Provider<ODatabaseObject> provider

    @Inject
    SelectTransactionalService selectService

    @Transactional
    public void insertRecord() {
        // insert record
        final ODatabaseObject db = provider.get()
        final Model model = new Model(name: 'John', nick: 'Doe')
        db.save(model)
    }

    @Transactional
    public Model subtransaction() {
        insertRecord()
        // inline transaction
        return selectService.select()
    }

    @Transactional
    public void rollbackCheck() {
        insertRecord()
        throw new IllegalStateException("Checking proper rollback")
    }

    @Transactional
    public void rollbackSubtransaction() {
        insertRecord()
        // inline transaction cause rollback
        selectService.rollbackCheck()
    }
}
