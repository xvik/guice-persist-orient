package ru.vyarus.guice.persist.orient.transaction

import com.google.inject.ProvisionException
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import ru.vyarus.guice.persist.orient.transaction.support.AllConnectionsService
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
@UseModules(PackageSchemeModule)
class ConnectionsTest extends AbstractTest {

    @Inject
    AllConnectionsService connectionsService

    def "get all tx connections"() {
        when: "obtaining all connections in transaction"
        connectionsService.allTxConnectionsObtain()
        then: "everything ok"
        true
    }

    def "get all no tx connections"() {
        when: "obtaining all connections without transaction"
        connectionsService.allNoTxConnectionsObtain()
        then: "everything ok"
        true
    }

    def "get connections without unit of work"() {
        when: "obtaining connection without unit of work"
        connectionsService.noTxConnectionObtain()
        then: "failed"
        thrown(ProvisionException)
    }

    def "get notx graph in transaction"() {
        when: "obtaining notx graph in transaction"
        connectionsService.noTxGraphObtainInTx()
        then: "failed"
        thrown(ProvisionException)
    }

    def "get tx graph in notx transaction"() {
        when: "obtaining tx graph in notx transaction"
        connectionsService.txGraphObtainInNoTx()
        then: "failed"
        thrown(ProvisionException)
    }
}
