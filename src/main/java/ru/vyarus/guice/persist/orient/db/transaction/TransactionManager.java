package ru.vyarus.guice.persist.orient.db.transaction;

import com.google.inject.ImplementedBy;
import com.google.inject.persist.UnitOfWork;
import com.orientechnologies.orient.core.tx.OTransaction;
import ru.vyarus.guice.persist.orient.db.transaction.internal.DefaultTransactionManager;

/**
 * Defines unit of work (transaction). Opened transaction does not mean opened connection - it just defines the scope
 * of transaction. Actual connection will be acquired on first connection request inside pool.
 * <p>
 * Each pool ({@link ru.vyarus.guice.persist.orient.db.pool.PoolManager}) maintains it's own connection type within
 * transaction (unit of work). Ideally all pools must rely on document pool
 * ({@link ru.vyarus.guice.persist.orient.db.pool.DocumentPool}) and create custom connection objects based on
 * single document connection. This makes all changes visible for all connection types (they actually use single
 * connection). Default pools implementation do that, but custom pools may not (anyway, it's recommended).
 * <p>
 * Commit and rollback operations have specifics, because of possible multi transaction unit of work (multiple pools
 * involved into transaction). Commit is called on all pools and if one of the pools will fail to perform commit,
 * other pools commit will be still called and only after that rollback called (to revert failed pool - other pools
 * will ignore that call). Default pool implementations are using single document connection so you may not
 * take into account connection types differences (it's all one connection).
 * <p>
 * Transaction could be defined with {@link com.google.inject.persist.Transactional} annotation or using
 * {@link ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate}
 * (or more specific {@link ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate}).
 * For both cases you can configure transaction with:
 * <ul>
 * <li>list of exception classes which will trigger or not trigger transaction rollback</li>
 * <li>type of transaction (orient support 3 types of transactions: no transaction, optimistic and pessimistic)</li>
 * </ul>
 * To change default transaction type when using annotations use {@link TxType} annotation together with
 * {@link com.google.inject.persist.Transactional}.
 * <p>
 * Default transaction type is optimistic. This could be changed in guice module.
 * <p>
 * Inline transactions are not supported: when unit of work is started with annotation or transaction template,
 * and inside some other annotated method or other transaction template appear its simply executed in scope of
 * current transaction (specific config is ignored).
 * <p>
 * There is a special case of external transaction: when connection is opened manually outside
 * of guice code and you need to re-use connection instance in the transaction (without handling usual transaction
 * semantic like commit and rollback). It's quite rare case and must be used with caution. External transaction
 * could be started with {@link TxConfig#external()} config passed into {@link #begin(TxConfig)} method.
 * Commit and rollback methods will not affect connection. External transaction must be supported by pool
 * implementations (supported by default implementations).
 * <p>
 * Default transaction manager implementation could be overridden by simply defining different
 * implementation in guice context.
 *
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
@ImplementedBy(DefaultTransactionManager.class)
public interface TransactionManager extends UnitOfWork {

    /**
     * Starts new transaction with default configuration. If transaction already in progress ignores call.
     * NOTE: orient transaction will be started only after obtaining connection from provider
     */
    @Override
    void begin();

    /**
     * Starts transaction with specific configuration. If transaction already in progress ignores call.
     * NOTE: orient transaction will be started only after obtaining connection from provider
     * <p>
     * Special case is external transaction: when connection is already created and bound to thread
     * ({@link com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal}) it could be used
     * instead of pool. In this case commit and rollback will not be performed (assuming transaction
     * is managed properly outside). To start external transaction use {@code TxConfig.external()}.
     *
     * @param config transaction configuration
     */
    void begin(TxConfig config);

    /**
     * Commits current transaction. Fails if no active transaction found (double call not allowed).
     * Fail performed to catch wrong bound for unit of work in application eagerly and should lead to better
     * transactions architecture.
     * <p>
     * If commit failed with error, error will be propagated. In case when two or more pools fail with errors,
     * only last error is propagated and other messages will just appear in log.
     * <p>
     * If transaction is external ({@link TxConfig#external()}) commit will be called on pools and they must
     * properly handle it (do not call commit on thread bound connection).
     */
    @Override
    void end();

    /**
     * Rollbacks current transaction (also ends unit of work). Fails if no active transaction found
     * (double call not allowed).
     * Fail performed to catch wrong bound for unit of work in application eagerly and should lead to better
     * transactions architecture.
     * <p>
     * If one or more pools rollback failed with errors, errors will be just logged without exception propagation.
     * (rollback operation is not recoverable, so there is no need to know exact reason in code).
     * <p>
     * In external transaction ({@link TxConfig#external()}) rollback is called on pools and pools must properly
     * implement this case (do not rollback external connection).
     */
    void rollback();

    /**
     * If transaction has specific configuration for rollback exception classes, it's checked and if rollback
     * shouldn't occur commit is performed instead of rollback. Ends unit of work.
     * Fails if no active transaction found (double call not allowed).
     * Fail performed to catch wrong bound for unit of work in application eagerly and should lead to better
     * transactions architecture.
     * <p>
     * In external transaction ({@link TxConfig#external()}) rollback is called on pools and pools must properly
     * implement this case (do not rollback external connection).
     *
     * @param ex occurred exception
     */
    void rollback(Throwable ex);

    /**
     * @return true if active transaction (ongoing unit of work), false otherwise
     */
    boolean isTransactionActive();

    /**
     * @return current transaction type or null if no ongoing transaction (unit of work)
     */
    OTransaction.TXTYPE getActiveTransactionType();

    /**
     * @return true if current transaction is external ({@link TxConfig#external()}), false if not or no
     * ongoing transaction.
     */
    boolean isExternalTransaction();
}
