package ru.vyarus.guice.persist.orient.repository

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.persist.PersistService
import ru.vyarus.guice.persist.orient.repository.benchmark.RepositoryBenchmark
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov
 * @since 23.09.2016
 */
class ChildInjectorRepositoryTest extends Specification {

    Injector injector

    void setup() {
        // child module!
        injector = Guice.createInjector().createChildInjector(new RepositoryTestModule(), new BootstrapModule())
        injector.getInstance(PersistService).start()
    }

    void cleanup() {
        if (injector != null) {
            injector.getInstance(PersistService).stop()
        }
        injector = null
    }

    def "Check dynamic binding scope"() {

        expect: "dynamic binding works"
        injector.getInstance(RepositoryBenchmark).findAll().size() == 10
    }
}