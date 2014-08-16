package ru.vyarus.guice.persist.orient.db.transaction.template;

import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Transaction template defines unit of work and properly inline all units of work during current unit scope.
 * It's the single point for all unit support (annotations support use it and specific tx template use it too).
 * So it's the only place which properly inline (sub)transactions.
 * <p>Specific provider must be used inside transactional action to obtain connection of required type(s)</p>
 */
@Singleton
public class TxTemplate {
    private TransactionManager transactionManager;

    @Inject
    public TxTemplate(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    return value type
     * @return value produced by action
     * @throws Throwable re-throws error thrown by action or after commit or rollback error
     */
    public <T> T doInTransaction(final TxAction<T> action) throws Throwable {
        return doInTransaction(null, action);
    }

    /**
     * @param config transaction config (ignored in case of ongoing transaction)
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    return value type
     * @return value produced by action
     * @throws Throwable re-throws error thrown by action or after commit or rollback error
     */
    public <T> T doInTransaction(final TxConfig config,
                                 final TxAction<T> action) throws Throwable {

        if (transactionManager.isTransactionActive()) {
            // execution inside of other transaction
            return action.execute();
        }

        try {
            transactionManager.begin(config);
            final T res = action.execute();
            transactionManager.end();
            return res;
        } catch (Throwable th) {
            // transaction may be not active if exception happened during commit and
            // tm already performed rollback action
            if (transactionManager.isTransactionActive()) {
                // calling once for nested transactions (or in case it was done manually
                transactionManager.rollback(th);
            }
            throw th;
        }
    }
}
