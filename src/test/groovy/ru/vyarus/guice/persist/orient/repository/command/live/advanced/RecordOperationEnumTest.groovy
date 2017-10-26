package ru.vyarus.guice.persist.orient.repository.command.live.advanced

import ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.RecordOperation
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 26.10.2017
 */
class RecordOperationEnumTest extends Specification{

    def "Check otype mapping correctness"() {

        when: "map from otype"
        def res = RecordOperation.forType(1 as byte)
        then: "correct mapping"
        res != null
        res.otype == 1 as byte

        when: "map unknown type"
        RecordOperation.forType(5 as byte)
        then: "error"
        thrown(IllegalArgumentException)

    }
}
