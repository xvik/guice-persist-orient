package ru.vyarus.guice.persist.orient.repository.command.ext.elvar

import ru.vyarus.guice.persist.orient.repository.command.core.el.ElUtils
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 22.09.2014
 */
class StringTemplateTest extends Specification {

    def "Check string analysis"() {

        when: "searching for placeholders"
        List<String> found = ElUtils.findVars(string)
        then: "found all placeholders"
        found == placeholders
        where:
        string                                 | placeholders
        'sample ${placeholder}'             | ['placeholder']
        'sample ${field1} and ${field2}'   | ['field1', 'field2']
        '$   {}'                              | []
    }

    def "Check string analysis duplicate"() {

        when: "analysing string wiht duplicate placeholders"
        List<String> vars = ElUtils.findVars('string with ${field} and ${field}')
        then: "duplicates allowed"
        vars == ['field']
    }

    def "Check placeholder substitution"() {

        when: "substituting placeholders"
        String res = ElUtils.replace(string, params)
        then: "substitution correct"
        res == result
        where:
        string                                 | params                                     | result
        'sample ${field}'                    | ['field': 'val']                         | 'sample val'
        'sample ${field} and ${field2}'    | ['field': 'val', 'field2': 'val2']      | 'sample val and val2'
        'sample ${field}'                    | ['field': '  val  ']                     | 'sample val'
        'sample ${field} and ${field}'     | ['field': '  val  ']                     | 'sample val and val'
    }

    def "Check substitution check"() {

        when: "substituting string with more placeholders than provided"
        ElUtils.replace('${fld1} ${fld2}', ['fld1': 'val'])
        then: "error"
        thrown(IllegalStateException)
    }

    def "Check validation"() {

        when: "providing valid input"
        ElUtils.validate('sample ${field}', ['field'])
        then: "validation success"
        true

        when: "provide not enough params"
        ElUtils.validate('sample ${field} ${fld2}', ['field'])
        then: "validation failed"
        thrown(IllegalStateException)

        when: "provide more params"
        ElUtils.validate('sample ${field}', ['field', 'fld2'])
        then: "validation failed"
        thrown(IllegalStateException)
    }
}