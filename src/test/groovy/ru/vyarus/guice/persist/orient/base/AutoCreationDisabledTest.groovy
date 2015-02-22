package ru.vyarus.guice.persist.orient.base

import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.exception.OStorageException
import ru.vyarus.guice.persist.orient.base.support.DisabledAutoCreationModule
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
        thrown(OStorageException)
    }
}