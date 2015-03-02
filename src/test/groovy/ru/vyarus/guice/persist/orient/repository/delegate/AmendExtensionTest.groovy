package ru.vyarus.guice.persist.orient.repository.delegate

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.delegate.support.amend.AmendedRepository
import ru.vyarus.guice.persist.orient.repository.delegate.support.amend.RootAmendRepo
import ru.vyarus.guice.persist.orient.repository.delegate.support.amend.ext.DummyAmendExtension
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@UseModules(RepositoryTestModule)
class AmendExtensionTest extends AbstractTest {

    @Inject
    RootAmendRepo repository

    def "Check amend extension resolution"() {

        when: "calling method with direct extension"
        DummyAmendExtension.used = null
        repository.select1()
        then: "amend extension resolved"
        DummyAmendExtension.used == "method"

        when: "calling method with type extension"
        DummyAmendExtension.used = null
        repository.select2()
        then: "amend extension resolved"
        DummyAmendExtension.used == "type"

        when: "calling method with root extension"
        DummyAmendExtension.used = null
        repository.select3()
        then: "amend extension resolved"
        DummyAmendExtension.used == "root"

    }
}