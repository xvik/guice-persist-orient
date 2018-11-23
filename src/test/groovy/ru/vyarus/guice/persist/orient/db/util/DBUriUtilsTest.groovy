package ru.vyarus.guice.persist.orient.db.util

import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 23.11.2018
 */
class DBUriUtilsTest extends Specification {


    def "Check uri parsing"() {

        expect:
        DBUriUtils.parseUri('memory:test') == ['memory:', 'test'] as String[]
        DBUriUtils.parseUri('plocal:/somewhere/test') == ['plocal:/somewhere', 'test'] as String[]
        DBUriUtils.parseUri('embedded:/somewhere/test') == ['embedded:/somewhere', 'test'] as String[]
        DBUriUtils.parseUri('remote:localhost/test') == ['remote:localhost', 'test'] as String[]
    }

    def "Check db type detection"() {

        expect:
        DBUriUtils.isMemory('memory:test')
        !DBUriUtils.isMemory('plocal:/somewhere/test')
        !DBUriUtils.isMemory('embedded:/somewhere/test')
        !DBUriUtils.isMemory('remote:localhost/test')

        and:
        !DBUriUtils.isRemote('memory:test')
        !DBUriUtils.isRemote('plocal:/somewhere/test')
        !DBUriUtils.isRemote('embedded:/somewhere/test')
        DBUriUtils.isRemote('remote:localhost/test')
    }
}
