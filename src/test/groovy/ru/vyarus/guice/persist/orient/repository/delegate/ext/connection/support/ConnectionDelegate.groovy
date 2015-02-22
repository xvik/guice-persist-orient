package ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.support

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.Connection
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
class ConnectionDelegate {


    List<Model> rawConnection(@Connection Object db) {
        [new Model(name: 'rawConnection')]
    }

    List<Model> subtypeMatch(@Connection ODatabaseObject db) {
        [new Model(name: 'subtypeMatch')]
    }

    List<Model> exactConnection(@Connection OObjectDatabaseTx db) {
        [new Model(name: 'exactConnection')]
    }

    // error: incompatible type
    List<Model> incompatible(@Connection OrientGraph db) {
    }

    // error: duplicate definition
    List<Model> duplicate(@Connection OObjectDatabaseTx db, @Connection OObjectDatabaseTx db2) {
    }
}
