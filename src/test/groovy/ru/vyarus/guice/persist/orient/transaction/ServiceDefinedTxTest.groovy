package ru.vyarus.guice.persist.orient.transaction

import com.google.inject.Inject
import com.google.inject.ProvisionException
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import ru.vyarus.guice.persist.orient.transaction.support.InlineTxDefinitionService
import spock.guice.UseModules

/**
 * Checks annotation interceptor when calling annotated methods from inside the bean. (impossible in spring)
 *
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
@UseModules([PackageSchemeModule, BootstrapModule])
class ServiceDefinedTxTest extends AbstractTest {

    @Inject
    InlineTxDefinitionService service

    def "Check tx initiated by service"() {
        when: "calling method without transaction"
        service.noTxMethod()
        then: "internal method calls create units of work"
        true

        when: "calling other method which calls other bean methods one of them not annotated"
        service.noMagicProofMethod()
        then: "method call failed, because third method didn't set its unit of work"
        thrown(ProvisionException)
    }
}