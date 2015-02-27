package ru.vyarus.guice.persist.orient.repository.command.ext.elvar

import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support.ObjVar
import ru.vyarus.guice.persist.orient.support.model.Model
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 27.02.2015
 */
class ConvertersTest extends Specification {

    def "Check default converter"() {

        when: "convert integer"
        def res = Converters.DEFAULT.convert(12)
        then: "correct string"
        res == "12"

        when: "convert boolean"
        res = Converters.DEFAULT.convert(true)
        then: "correct string"
        res == "true"

        when: "convert string"
        res = Converters.DEFAULT.convert("some string")
        then: "correct string"
        res == "some string"

        when: "convert object"
        res = Converters.DEFAULT.convert(new ObjVar(value: "test"))
        then: "correct string"
        res == "test"

        when: "convert null"
        res = Converters.DEFAULT.convert(null)
        then: "correct string"
        res == null
    }

    def "Check class conversion"() {

        when: "convert class"
        def res = Converters.CLASS.convert(Model)
        then: "correct string"
        res == "Model"
    }
}