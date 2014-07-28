package ru.vyarus.guice.persist.orient.base

import com.google.inject.persist.PersistService
import ru.vyarus.guice.persist.orient.support.modules.EmptyAutoScanModule
import spock.guice.UseModules
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(EmptyAutoScanModule)
class EmptyAutoScanModelTest extends Specification {

    @Inject
    PersistService persist

    def "Empty model check"() {
        when: "initializing database"
        persist.start()
        then: "no model found"
        thrown(IllegalStateException)
    }
}
