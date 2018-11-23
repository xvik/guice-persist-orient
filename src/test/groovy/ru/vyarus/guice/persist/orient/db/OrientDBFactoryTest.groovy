package ru.vyarus.guice.persist.orient.db

import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 23.11.2018
 */
class OrientDBFactoryTest extends Specification {

    def "Test simple factory cases"() {

        when: "creating simple config"
        OrientDBFactory factory = new OrientDBFactory("memory:test", "admin", "adminpass", true,
                OrientDBConfig.defaultConfig(), null, null, null)
        then:
        factory.autoCreate
        factory.uri == 'memory:test'
        factory.dbType == ODatabaseType.MEMORY
        factory.dbUrl == 'memory:'
        factory.dbName == 'test'
        factory.user == 'admin'
        factory.password == 'adminpass'
        factory.memory
        !factory.remote

        when: "creating orient object"
        OrientDB db = factory.createOrientDB()
        then:
        db.open

        cleanup:
        db.close()
    }

    def "Check other db types"() {

        when: "creating plocal config"
        OrientDBFactory factory = new OrientDBFactory("plocal:/somewhere/test", "admin", "adminpass", true,
                OrientDBConfig.defaultConfig(), null, null, null)
        then:
        !factory.remote
        !factory.memory
        factory.autoCreate

        when: "creating remote config"
        factory = new OrientDBFactory("remote:localhost/test", "admin", "adminpass", true,
                OrientDBConfig.defaultConfig(), null, null, null)
        then:
        factory.remote
        !factory.memory
        !factory.autoCreate
        factory.dbType == null // because remote credentials not set (and so type is not important)
    }

    def "Check direct remote server user"() {

        when: 'enable remote creation'
        OrientDBFactory factory = new OrientDBFactory("remote:localhost/test", "admin", "adminpass", false,
                OrientDBConfig.defaultConfig(), "root", "root", ODatabaseType.MEMORY)
        then:
        factory.remote
        !factory.memory
        factory.autoCreate
        factory.dbType == ODatabaseType.MEMORY

        when: 'creating instance'
        def db = factory.createOrientDB()
        then:
        db.serverUser == 'root'
        db.serverPassword == 'root'

        when: 'do indirect config'
        OrientDBFactory.enableAutoCreationRemoteDatabase('tat', 'tat', ODatabaseType.PLOCAL)
        db.close()
        db = factory.createOrientDB()
        then: 'direct configuration not overridden'
        factory.dbType == ODatabaseType.MEMORY
        db.serverUser == 'root'
        db.serverPassword == 'root'

        cleanup:
        db.close()


    }

    def "Check no local auto creation"() {

        when: "creating factory without auto creation "
        OrientDBFactory factory = new OrientDBFactory("memory:test", "admin", "adminpass", false,
                OrientDBConfig.defaultConfig(), null, null, null)
        then:
        !factory.autoCreate
    }

    def "Check indirect remote auto creation"() {

        when: "creating factory without auto creation "
        OrientDBFactory factory = new OrientDBFactory("remote:localhost/test", "admin", "adminpass", false,
                OrientDBConfig.defaultConfig(), null, null, null)
        then:
        !factory.autoCreate

        when: 'enable indirect auto creation'
        OrientDBFactory.enableAutoCreationRemoteDatabase('tat', 'tatp', ODatabaseType.PLOCAL)
        def db = factory.createOrientDB()
        then:
        factory.autoCreate
        factory.dbType == ODatabaseType.PLOCAL
        db.serverUser == 'tat'
        db.serverPassword == 'tatp'

        when: 'cleanup indirect config'
        OrientDBFactory.disableAutoCreationRemoteDatabase()
        db.close()
        db = factory.createOrientDB()
        then: 'existing factory not updated'
        factory.autoCreate

        when: 'new factory does not see indirect config'
        factory = new OrientDBFactory("remote:localhost/test", "admin", "adminpass", false,
                OrientDBConfig.defaultConfig(), null, null, null)
        db.close()
        db = factory.createOrientDB()
        then:
        !factory.autoCreate

        cleanup:
        db.close()


    }
}
