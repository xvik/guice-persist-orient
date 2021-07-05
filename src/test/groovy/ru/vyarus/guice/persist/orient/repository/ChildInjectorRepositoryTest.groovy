package ru.vyarus.guice.persist.orient.repository

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.persist.PersistService
import com.orientechnologies.orient.core.config.OGlobalConfiguration
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
        // TODO for now reverted 3.2 behaviours, but no-users mode should be supported directly
        OGlobalConfiguration.SCRIPT_POLYGLOT_USE_GRAAL.setValue(false)
        OGlobalConfiguration.CREATE_DEFAULT_USERS.setValue(true)

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