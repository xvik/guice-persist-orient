package ru.vyarus.guice.persist.orient.finder.internal

import com.google.common.collect.Sets
import ru.vyarus.guice.persist.orient.finder.scanner.FinderScanner
import ru.vyarus.guice.persist.orient.support.finder.inheritance.PowerFinder
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 17.10.2014
 */
class ScannerTest extends Specification {

    def "Check scanner correctness"() {

        when: "scan for finders"
        List<Class<?>> finders = FinderScanner.scan("ru.vyarus.guice.persist.orient.support.finder.inheritance")
        then: "only valid finders resolved"
        finders == [PowerFinder]
    }
}