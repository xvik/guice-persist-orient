package ru.vyarus.guice.persist.orient.db

import com.google.inject.persist.PersistService
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(DefaultModule)
class StartupTest extends Specification {

    @Inject
    PersistService persist;

    def "Startup test"() {
        persist.start()
        persist.stop()

        expect: "no errors"
        true
    }
}
