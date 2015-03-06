package ru.vyarus.guice.persist.orient.db.scheme

import com.google.inject.persist.PersistService
import ru.vyarus.guice.persist.orient.db.support.EmptyPackageModule
import spock.guice.UseModules
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(EmptyPackageModule)
class EmptyPackageModelTest extends Specification {

    @Inject
    PersistService persist

    def "Empty model check"() {
        when: "initializing database"
        persist.start()
        then: "no model found"
        thrown(SchemeInitializationException)
    }
}
