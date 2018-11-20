package ru.vyarus.guice.persist.orient.transaction.support

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.db.object.ODatabaseObject

/**
 * Case impossible in spring: methods called inside bean still affected by transaction interceptor,
 * so service could define units of work in such easy and elegant way
 *
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
@javax.inject.Singleton
class InlineTxDefinitionService {

    @Inject
    Provider<ODatabaseObject> provider;

    def noTxMethod() {
        txMethod1()
        txMethod2()
    }

    def noMagicProofMethod() {
        txMethod1()
        txMethod2()
        notxMethod3()
    }

    @Transactional
    def txMethod1() {
        provider.get()
    }

    @Transactional
    def txMethod2() {
        provider.get()
    }

    def notxMethod3() {
        provider.get()
    }
}
