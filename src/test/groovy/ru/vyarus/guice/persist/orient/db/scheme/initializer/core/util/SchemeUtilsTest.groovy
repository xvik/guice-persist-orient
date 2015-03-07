package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.support.hierarchy.Base
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.support.hierarchy.Derived
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
class SchemeUtilsTest extends Specification {

    def "Check hierarchy parsing"() {

        when: "parsing simple class"
        def res = SchemeUtils.resolveHierarchy(Base)
        then: "one class returned"
        res == [Base]

        when: "parsing complex class"
        res = SchemeUtils.resolveHierarchy(Derived)
        then: "heirarchy parsed"
        res == [Derived, Base]
    }
}