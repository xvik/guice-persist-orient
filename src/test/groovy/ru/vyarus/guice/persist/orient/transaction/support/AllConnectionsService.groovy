package ru.vyarus.guice.persist.orient.transaction.support

import com.google.inject.Provider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.tx.OTransaction
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx
import ru.vyarus.guice.persist.orient.db.transaction.TxType

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
@javax.inject.Singleton
class AllConnectionsService {

    //@formatter:off
    @Inject Provider<ODatabaseDocument> documentProvider
    @Inject Provider<ODatabaseObject> objectProvider
    @Inject Provider<OrientGraph> txGraphProvider
    @Inject Provider<OrientGraphNoTx> noTxGraphProvider
    @Inject Provider<OrientBaseGraph> baseGraphProvider
    //@formatter:on

    def noTxConnectionObtain() {
        // will fail (no unit of work defined)
        documentProvider.get()
    }

    @Transactional
    def noTxGraphObtainInTx() {
        // fail to get in transaction
        noTxGraphProvider.get()
    }

    @Transactional
    @TxType(OTransaction.TXTYPE.NOTX)
    def txGraphObtainInNoTx() {
        // fail to get in notx mode
        txGraphProvider.get()
    }

    @Transactional
    def allTxConnectionsObtain() {
        documentProvider.get()
        objectProvider.get()
        baseGraphProvider.get()
        txGraphProvider.get()
    }

    @Transactional
    @TxType(OTransaction.TXTYPE.NOTX)
    def allNoTxConnectionsObtain() {
        documentProvider.get()
        objectProvider.get()
        baseGraphProvider.get()
        noTxGraphProvider.get()
    }
}
