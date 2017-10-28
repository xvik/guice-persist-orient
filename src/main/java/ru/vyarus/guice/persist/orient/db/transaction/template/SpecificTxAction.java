package ru.vyarus.guice.persist.orient.db.transaction.template;

/**
 * Action to be executed in transaction by {@link ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate}.
 * Alternative to use of @Transactional annotation.
 *
 * @param <T> action return value type
 * @param <C> required connection type
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
public interface SpecificTxAction<T, C> {

    /**
     * @param db database connection instance
     * @return value (or null if T is Void)
     * @throws Throwable error thrown in action may cause rollback or commit, depending on transaction configuration
     */
    T execute(C db) throws Throwable;
}
