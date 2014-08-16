package ru.vyarus.guice.persist.orient.db.transaction;

import com.google.inject.ImplementedBy;
import com.google.inject.persist.UnitOfWork;
import com.orientechnologies.orient.core.tx.OTransaction;
import ru.vyarus.guice.persist.orient.db.transaction.internal.DefaultTransactionManager;

/**
 * Defines unit of work for orient transactions. Each registered pool maintains it's own transaction.
 * If you use different pools withing single transaction, pools connections will not see each others changes.
 * But with more fine grained transactions it will not be a problem.
 * <p>For example, you can use graph connection to perform complex searches and object connection to perform changes.
 * Or you can modify different classes within different connections (and it will be ok within single unit of work).</p>
 * <p>Most likely, for most cases you will use single type of connection and so single orient transaction.</p>
 * <p>Commit and rollback operations have specifics, because of multi transaction unit of work: for example you use
 * two or more different connection types; if during commit first transaction will fail, second transaction will be
 * still committed and rollback performed only on first one (that is because transactions are actually independent
 * and transactional manager simply provides easy way to gain all possible benefits of orient
 * with the simplest usage).</p>
 * Transaction could be defined with @Transactional annotation or using
 * {@code ru.vyarus.guice.persist.orient.db.transaction.template.TxTemplate}
 * (or more specific {@code ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate}).
 * For both cases you can configure transaction with:
 * <ul>
 * <li>list of exception classes which will trigger or not trigger transaction rollback</li>
 * <li>type of transaction (orient support 3 types of transactions: no transaction, optimistic and pessimistic)</li>
 * </ul>
 * To change default transaction type when using annotations use @TxType annotation together with @Transactional.
 * <p>Default transaction type is optimistic. This could be change in guice module.</p>
 * <p>Inline transactions are not supported: when unit of work is started with annotation or transaction template,
 * and inside some other annotated method or other transaction template appear its simply executed in scope of
 * current transaction (it's specific config is ignored).</p>
 * <p>Default transaction manager implementation could be overridden by simply defining different
 * implementation in guice context.</p>
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
     *
     * @param config transaction configuration
     */
    void begin(TxConfig config);

    /**
     * Commits current transaction. Fails if no active transaction found (double call not allowed).
     * Fail performed to catch wrong bound for unit of work in application eagerly and should lead to better
     * transactions architecture.
     * <p>If commit failed with error, error will be propagated. In case when two or more pools fail with errors,
     * only last error is propagated and other messages will just appear in log</p>
     */
    @Override
    void end();

    /**
     * Rollbacks current transaction (also ends unit of work). Fails if no active transaction found
     * (double call not allowed).
     * Fail performed to catch wrong bound for unit of work in application eagerly and should lead to better
     * transactions architecture.
     * <p>If one or more pools rollback failed with errors, errors will be just logged without exception propagation.
     * (rollback operation is not recoverable, so there is no need to know exact reason in code)</p>
     */
    void rollback();

    /**
     * If transaction has specific configuration for rollback exception classes, it's checked and if rollback
     * shouldn't occur commit is performed instead of rollback. Ends unit of work.
     * Fails if no active transaction found (double call not allowed).
     * Fail performed to catch wrong bound for unit of work in application eagerly and should lead to better
     * transactions architecture.
     *
     * @param ex occurred exception
     */
    void rollback(Throwable ex);

    /**
     * @return true if active transaction (ongoing unit of work), false otherwise
     */
    boolean isTransactionActive();

    /**
     * @return current transaction type or null if no ongoing transaction (unti of work)
     */
    OTransaction.TXTYPE getActiveTransactionType();
}
