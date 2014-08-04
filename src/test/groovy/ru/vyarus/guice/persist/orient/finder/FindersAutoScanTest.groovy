package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.support.finder.InterfaceFinder
import ru.vyarus.guice.persist.orient.support.finder.subpkg.FoundFinder
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 04.08.2014
 */
@UseModules(AutoScanFinderTestModule)
class FindersAutoScanTest extends AbstractTest {

    @Inject
    InterfaceFinder finder
    @Inject
    FoundFinder foundFinder

    def "Check all finders initialized"() {

        when: "module initialized, both finders available for use"
        finder.selectAll()
        foundFinder.findAll()
        then: "success"
        true
    }
}