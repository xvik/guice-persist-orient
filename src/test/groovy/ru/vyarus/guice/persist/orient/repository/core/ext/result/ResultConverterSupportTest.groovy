package ru.vyarus.guice.persist.orient.repository.core.ext.result

import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException
import ru.vyarus.guice.persist.orient.repository.core.ext.result.support.ConvRepo
import ru.vyarus.guice.persist.orient.repository.core.ext.result.support.ConvRepo2
import ru.vyarus.guice.persist.orient.repository.core.ext.result.support.ext.DummyConverter
import ru.vyarus.guice.persist.orient.repository.core.ext.result.support.RootConvRepo
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtUtils
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
class ResultConverterSupportTest extends Specification {

    def "Check result converter resolution"() {

        when: "inspect method with direct converter"
        DummyConverter res = ExtUtils.findResultConverter(ConvRepo.getMethod("select1"), RootConvRepo)
        then: "method ext found"
        res.value() == "method"

        when: "inspect method with type converter"
        res = ExtUtils.findResultConverter(ConvRepo.getMethod("select2"), RootConvRepo)
        then: "type ext found"
        res.value() == "type"

        when: "inspect method with root converter"
        res = ExtUtils.findResultConverter(ConvRepo2.getMethod("select3"), RootConvRepo)
        then: "root ext found"
        res.value() == "root"

        when: "inspect method with illegal declaration"
        ExtUtils.findResultConverter(ConvRepo.getMethod("illegal"), RootConvRepo)
        then: "error"
        thrown(MethodDefinitionException)
    }
}