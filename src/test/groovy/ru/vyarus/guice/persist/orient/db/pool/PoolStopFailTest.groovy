package ru.vyarus.guice.persist.orient.db.pool

import com.google.inject.Inject
import com.google.inject.persist.PersistService
import ru.vyarus.guice.persist.orient.db.pool.support.FailedPoolStopModule
import spock.guice.UseModules
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(FailedPoolStopModule)
class PoolStopFailTest extends Specification {

    @Inject
    PersistService persist

    def "Check pool failed on stop"() {

        when: "starting pools then stop"
        persist.start()
        persist.stop()
        then: "pool fail handled"
        true
    }
}