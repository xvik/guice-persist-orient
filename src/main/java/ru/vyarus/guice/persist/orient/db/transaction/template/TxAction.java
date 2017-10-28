package ru.vyarus.guice.persist.orient.db.transaction.template;

/**
 * Action to be executed in transaction by {@link ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate}.
 * Alternative to use of @Transactional annotation.
 *
 * @param <T> action return value type
 */
public interface TxAction<T> {

    /**
     * Connection(s) must be obtained from appropriate provider(s) (otherwise use
     * {@link ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction} for single connection actions).
     *
     * @return value (or null if T is Void)
     * @throws Throwable error thrown in action may cause rollback or commit, depending on transaction configuration
     */
    T execute() throws Throwable;
}
