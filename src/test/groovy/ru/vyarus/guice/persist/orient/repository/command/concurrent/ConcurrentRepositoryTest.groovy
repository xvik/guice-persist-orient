package ru.vyarus.guice.persist.orient.repository.command.concurrent

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.command.support.DbRecognitionCases
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@UseModules(RepositoryTestModule)
class ConcurrentRepositoryTest extends AbstractTest {

    @Inject
    DbRecognitionCases repository;

    ExecutorService executor

    @Override
    void setup() {
        executor = Executors.newFixedThreadPool(20)
    }

    @Override
    void cleanup() {
        executor.shutdown()
    }

    def "Check repositories concurrency"() {

        when: "Call repository in 20 threads"
        List<Future<?>> executed = []
        int times = 20
        times.times({
            executed << executor.submit({
                repository.selectAll()
            })
        })
        // lock until finish
        executed.each({ it.get() })
        then: "Nothing fails"
        true

    }
}