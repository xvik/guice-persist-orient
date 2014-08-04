package ru.vyarus.guice.persist.orient.concurrent

import com.google.inject.Inject
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.InterfaceFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.TestFinderModule
import spock.guice.UseModules
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


/**
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@UseModules(TestFinderModule)
class ConcurrentFindersTest extends AbstractTest {

    @Inject
    InterfaceFinder finder;

    ExecutorService executor

    @Override
    void setup() {
        executor = Executors.newFixedThreadPool(20)
    }

    @Override
    void cleanup() {
        executor.shutdown()
    }

    def "Check finders concurrency"() {

        when: "Call finder in 20 threads"
        List<Future<?>> executed = []
        int times = 20
        times.times({
            executed << executor.submit({
                finder.selectAll()
            })
        })
        // lock until finish
        executed.each({ it.get() })
        then: "Nothing fails"
        true

    }
}