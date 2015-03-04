package ru.vyarus.guice.persist.orient.db.retry

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.db.retry.support.ExceptionCases
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 04.03.2015
 */
@UseModules(DefaultModule)
class ExceptionsRecognitionTest extends Specification {

    @Inject
    ExceptionCases cases

    def "Check exceptions recognition"() {

        when: "call method with wrapped retry exception"
        cases.wrappedRetry()
        then: "retry applied"
        thrown(IllegalStateException)
        cases.callCount == 11

        when: "call method with different exception"
        cases.callCount = 0
        cases.otherException()
        then: "retry not applied"
        thrown(IllegalStateException)
        cases.callCount == 1
    }
}