package ru.vyarus.guice.persist.orient.db.transaction;

import com.google.common.base.MoreObjects;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.core.tx.OTransactionNoTx;

import java.util.Collections;
import java.util.List;

/**
 * Transaction configuration. Intended to be used with transaction templates to configure transaction.
 * The same configuration could be achieved with @Transactional and @TxType annotations.
 *
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class TxConfig {
    private List<Class<? extends Exception>> rollbackOn;
    private List<Class<? extends Exception>> ignore;
    private OTransaction.TXTYPE txtype;
    // transaction is managed externally
    private boolean external;

    public TxConfig() {
        this(null, null, null);
    }

    /**
     * Changes default transaction type (specified in module).
     *
     * @param txtype type to use within transaction (NOTX type disables transaction)
     */
    public TxConfig(final OTransaction.TXTYPE txtype) {
        this(null, null, txtype);
    }

    /**
     * @param rollbackOn rollback only on these exceptions (otherwise commit)
     * @param ignore     do not rollback on these exceptions
     * @see com.google.inject.persist.Transactional javadoc for details
     */
    public TxConfig(final List<Class<? extends Exception>> rollbackOn,
                    final List<Class<? extends Exception>> ignore) {
        this(rollbackOn, ignore, null);
    }

    /**
     * @param rollbackOn rollback only on these exceptions (otherwise commit)
     * @param ignore     do not rollback on these exceptions
     * @param txtype     type to use within transaction (NOTX type disables transaction)
     * @see com.google.inject.persist.Transactional javadoc for details
     */
    public TxConfig(final List<Class<? extends Exception>> rollbackOn,
                    final List<Class<? extends Exception>> ignore,
                    final OTransaction.TXTYPE txtype) {
        this.rollbackOn = MoreObjects.firstNonNull(rollbackOn, Collections.<Class<? extends Exception>>emptyList());
        this.ignore = MoreObjects.firstNonNull(ignore, Collections.<Class<? extends Exception>>emptyList());
        this.txtype = MoreObjects.firstNonNull(txtype, OTransaction.TXTYPE.OPTIMISTIC);
    }

    /**
     * @return list of exception to rollback on or null if not defined
     */
    public List<Class<? extends Exception>> getRollbackOn() {
        return rollbackOn;
    }

    /**
     * @return list of exception to not rollback on or null if not defined
     */
    public List<Class<? extends Exception>> getIgnore() {
        return ignore;
    }

    /**
     * @return transaction type or null if not defined
     */
    public OTransaction.TXTYPE getTxtype() {
        return txtype;
    }

    /**
     * External transactions allow re-using already existing and bound to thread database instance.
     * But in this case no commit or rollback will be performed.
     *
     * @return true when transaction is managed externally, false otherwise
     */
    public boolean isExternal() {
        return external;
    }

    @Override
    public String toString() {
        return external ? "{external}" : "{type " + txtype
                + (rollbackOn.isEmpty() ? "" : " rollbackOn " + rollbackOn)
                + (ignore.isEmpty() ? "" : " ignore " + ignore) + "}";
    }

    /**
     * External transaction is a re-use of database instance already bound to thread. For example,
     * <pre>{@code
     *      // transaction opened manually
     *      ODatabaseDocumentTx db = new ODatabaseDocumentTx();
     *      // only after that it could be detected
     *      db.activateOnCurrentThread();
     *
     *      // context is PersistentContext, but TxTemplate may be called directly
     *      context.doInTransaction(TxConfig.external(), () -> {
     *          // here we can use external transaction
     *      })
     *
     *      // connection closed manually
     *      db.close();
     * }</pre>
     * <p>
     * External transaction is a way to re-use already existing connection (e.g. managed by orient itself) in
     * guice beans. The case must be applied with care: as connection is managed outside of guice, external
     * transaction scope must be exactly between manual db open and close (otherwise you will have error due
     * to not bound db instance). Rollback and commit actions will not affect connection.
     * <p>
     * Type of transaction is resolved from external connection.
     * <p>
     * One example usage case is non blocking async queries listener: there orient create connection in separate
     * thread and calls listener multiple times, so if usual transaction would be used inside listener,
     * it will override existing orient transaction and orient logic execution will break.
     *
     * @return external transaction config
     */
    public static TxConfig external() {
        final TxConfig cfg = new TxConfig();
        cfg.external = true;
        // currently only only notx and optimistic transactions supported
        cfg.txtype = ODatabaseRecordThreadLocal.instance().get()
                .getTransaction() instanceof OTransactionNoTx
                ? OTransaction.TXTYPE.NOTX : OTransaction.TXTYPE.OPTIMISTIC;
        cfg.rollbackOn.clear();
        cfg.ignore.clear();
        return cfg;
    }
}
