package ru.vyarus.guice.persist.orient.db

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.exception.ODatabaseException
import ru.vyarus.guice.persist.orient.db.support.DisabledAutoCreationModule
import spock.guice.UseModules
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 27.09.2014
 */
@UseModules(DisabledAutoCreationModule)
class AutoCreationDisabledTest extends Specification {

    @Inject
    PersistService persist

    def "Disabled auto creation check"() {
        when: "initializing with not existent database"
        persist.start()
        then: "failed to start"
        thrown(ODatabaseException)
    }
}