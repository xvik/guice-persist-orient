package ru.vyarus.guice.persist.orient.transaction.support

import com.google.inject.AbstractModule
import com.orientechnologies.orient.core.tx.OTransaction
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig
import ru.vyarus.guice.persist.orient.support.Config

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
class NoTxModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new OrientModule(Config.DB, Config.USER, Config.PASS)
        .defaultTransactionConfig(new TxConfig(OTransaction.TXTYPE.NOTX)))
    }
}
