package ru.vyarus.guice.persist.orient.repository.core.ext.result.ext.off

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@UseModules(RepositoryTestModule)
class NoConversionTest extends AbstractTest {

    @Inject
    NoConversionCases repository

    def "Check default converter switch off"() {

        when: "converter enabled"
        repository.select()
        then: "ok"
        true

        when: "switch off converter"
        repository.selectNoConversion()
        then: "incompatible types"
        thrown(MethodExecutionException)

    }
}