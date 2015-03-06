package ru.vyarus.guice.persist.orient.db.pool

import com.google.inject.Inject
import com.google.inject.persist.PersistService
import ru.vyarus.guice.persist.orient.db.pool.support.NoTypePoolModule
import spock.guice.UseModules
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(NoTypePoolModule)
class PoolTypeCheckTest extends Specification{

    @Inject
    PersistService persist

    def "Check pool without type reject"() {

        when: "start with pool without type"
        persist.start()
        then: "pool rejected"
        thrown(NullPointerException)
    }
}
