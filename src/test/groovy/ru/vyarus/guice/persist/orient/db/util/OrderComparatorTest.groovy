package ru.vyarus.guice.persist.orient.db.util

import ru.vyarus.guice.persist.orient.db.util.support.First
import ru.vyarus.guice.persist.orient.db.util.support.Second
import ru.vyarus.guice.persist.orient.db.util.support.Third
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
class OrderComparatorTest extends Specification {

    def "Check order sorting"() {

        when: "sorting unsorted list"
        def list = [new Third(), new First(), new Second()]
        Collections.sort(list, new OrderComparator())
        OrderComparator
        then: "sorted"
        list[0].class == First
        list[1].class == Second

    }
}