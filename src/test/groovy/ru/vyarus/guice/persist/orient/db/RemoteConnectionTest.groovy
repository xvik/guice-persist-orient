package ru.vyarus.guice.persist.orient.db

import com.google.inject.Inject
import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.exception.OStorageException
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules
import spock.lang.Specification

/**
 * Checking that we don't try creating remote database
 * @author Vyacheslav Rusakov 
 * @since 15.09.2014
 */
@UseModules(DefaultModule)
class RemoteConnectionTest extends Specification {

    @Inject
    PersistService persist

    //using remote db url
    def static normalUrl;
    static {
        normalUrl = Config.DB
        Config.DB = "remote:localhost/not_existent"
    }

    void cleanupSpec() {
        Config.DB = normalUrl
    }

    def "Check remote connection avoid automatic database creation"() {

        when: "starting persistence"
        persist.start()
        then: "connection failed, but not creation"
        thrown(OStorageException)
    }

}