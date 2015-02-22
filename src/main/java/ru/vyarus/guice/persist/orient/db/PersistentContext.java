package ru.vyarus.guice.persist.orient.db;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.tx.OTransaction;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction;
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate;

import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Combines most useful api together for simplified usage.
 * <p>Example usage:
 * {@code @Inject PersistentContext<OObjectDatabaseTx> context;}</p>
 * <p/>
 * <p>The following connection types could be used:</p>
 * <ul>
 * <li>{@link com.orientechnologies.orient.object.db.OObjectDatabaseTx} for object db connection</li>
 * <li>{@link com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx} for document db connection</li>
 * <li>{@link com.tinkerpop.blueprints.impls.orient.OrientBaseGraph} for graph db connection
 * (transactional or not)</li>
 * <li>{@link com.tinkerpop.blueprints.impls.orient.OrientGraph} for transactional graph db connection
 * (will fail if notx transaction type)</li>
 * <li>{@link com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx} for non transactional graph db connection
 * (will provide only for notx transaction type, otherwise fail)</li>
 * </ul>
 * <p>See {@link ru.vyarus.guice.persist.orient.db.pool.PoolManager} implementations for connection details.</p>
 *
 * @param <C> connection type
 * @author Vyacheslav Rusakov
 * @see com.google.inject.persist.Transactional
 * @see ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate
 * @see ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
 * @see ru.vyarus.guice.persist.orient.db.transaction.TransactionManager
 * @since 11.02.2015
 */
@Singleton
public class PersistentContext<C> {

    private final TxTemplate txTemplate;
    private final SpecificTxTemplate<C> template;
    private final Provider<C> provider;
    private final TransactionManager transactionManager;

    @Inject
    public PersistentContext(final TxTemplate txTemplate, final SpecificTxTemplate<C> template,
                             final Provider<C> provider, final TransactionManager transactionManager) {
        this.txTemplate = txTemplate;
        this.template = template;
        this.provider = provider;
        this.transactionManager = transactionManager;
    }

    /**
     * Method must be called withing transaction scope (either under @Transaction annotation or in explicitly defined
     * transaction template). If current transaction doesn't have assigned connection instance, new connection will
     * be obtained. During transaction method returns the same connection instance.
     *
     * @return current transaction connection
     */
    public C getConnection() {
        return provider.get();
    }

    /**
     * @return transaction manager instance
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * Execute action within transaction.
     *
     * @param config transaction config (ignored in case of ongoing transaction)
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    expected return type
     * @return value produced by action
     * @see ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate
     */
    public <T> T doInTransaction(final TxConfig config, final TxAction<T> action) {
        return txTemplate.doInTransaction(config, action);
    }

    /**
     * Execute specific action within transaction.
     *
     * @param config transaction config (ignored in case of ongoing transaction)
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    expected return type
     * @return value produced by action
     * @see ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
     */
    public <T> T doInTransaction(final TxConfig config, final SpecificTxAction<T, C> action) {
        return template.doInTransaction(config, action);
    }

    /**
     * Execute action with transaction.
     *
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    expected return type
     * @return value produced by action
     * @see ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate
     */
    public <T> T doInTransaction(final TxAction<T> action) {
        return txTemplate.doInTransaction(action);
    }

    /**
     * Execute specific action within transaction.
     *
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    expected return type
     * @return value produced by action
     * @see ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
     */
    public <T> T doInTransaction(final SpecificTxAction<T, C> action) {
        return template.doInTransaction(action);
    }

    /**
     * Execute action without transaction.
     * <p>NOTE: If normal transaction already started, error will be thrown to prevent confusion
     * (direct call to template will ignore notx config in case of ongoing transaction, so this call is safer)</p>
     *
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    expected return type
     * @return value produced by action
     * @see ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate
     */
    public <T> T doWithoutTransaction(final TxAction<T> action) {
        checkNotx();
        return txTemplate.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), action);
    }

    /**
     * Execute action without transaction.
     * <p>NOTE: If normal transaction already started, error will be thrown to prevent confusion
     * (direct call to template will ignore notx config in case of ongoing transaction, so this call is safer)</p>
     *
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    expected return type
     * @return value produced by action
     * @see ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
     */
    public <T> T doWithoutTransaction(final SpecificTxAction<T, C> action) {
        checkNotx();
        return template.doInTransaction(new TxConfig(OTransaction.TXTYPE.NOTX), action);
    }

    private void checkNotx() {
        Preconditions.checkState(!transactionManager.isTransactionActive()
                        || transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.NOTX,
                "Can't execute without transaction, because normal transaction already started");
    }
}
