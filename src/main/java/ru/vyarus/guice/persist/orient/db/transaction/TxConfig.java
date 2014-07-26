package ru.vyarus.guice.persist.orient.db.transaction;

import com.orientechnologies.orient.core.tx.OTransaction;

import java.util.List;

/**
 * Transaction configuration. Intended to be used with transaction templates to configure transaction.
 * The same configuration could be achieved with @Transactional and @TxType annotations.
 *
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
public class TxConfig {
    private List<Class<? extends Exception>> rollbackOn;
    private List<Class<? extends Exception>> ignore;
    private OTransaction.TXTYPE txtype;

    public TxConfig() {
    }

    /**
     * Changess default transaction type (specified in module)
     *
     * @param txtype type to use within transaction (NOTX type disables transaction)
     */
    public TxConfig(final OTransaction.TXTYPE txtype) {
        this.txtype = txtype;
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
        this.rollbackOn = rollbackOn;
        this.ignore = ignore;
        this.txtype = txtype;
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
}
