package ru.vyarus.guice.persist.orient.db.transaction.template;

import com.google.common.base.Throwables;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Transaction template defines unit of work and properly inline all units of work during current unit scope.
 * It's the single point for all unit support (annotations support use it and specific tx template use it too).
 * So it's the only place which properly inline (sub)transactions.
 * <p>
 * Specific provider must be used inside transactional action to obtain connection of required type(s).
 */
@Singleton
public class TxTemplate {
    private final TransactionManager transactionManager;

    @Inject
    public TxTemplate(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Error is propagated if runtime or rethrown as runtime exception.
     *
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    return value type
     * @return value produced by action
     */
    public <T> T doInTransaction(final TxAction<T> action) {
        return doInTransaction(null, action);
    }

    /**
     * Error is propagated if runtime or rethrown as runtime exception.
     *
     * @param config transaction config (ignored in case of ongoing transaction)
     * @param action action to execute within transaction (new or ongoing)
     * @param <T>    return value type
     * @return value produced by action
     */
    public <T> T doInTransaction(final TxConfig config,
                                 final TxAction<T> action) {

        T res = null;
        if (transactionManager.isTransactionActive()) {
            // execution inside of other transaction
            try {
                res = action.execute();
            } catch (Throwable th) {
                throwRuntime(th);
            }
        } else {
            try {
                transactionManager.begin(config);
                res = action.execute();
                transactionManager.end();

            } catch (Throwable th) {
                // transaction may be not active if exception happened during commit and
                // tm already performed rollback action
                if (transactionManager.isTransactionActive()) {
                    // calling once for nested transactions (or in case it was done manually
                    transactionManager.rollback(resolveErrorToAnalyze(th));
                }
                throwRuntime(th);
            }
        }
        return res;
    }

    private void throwRuntime(final Throwable th) {
        Throwables.throwIfUnchecked(th);
        throw new TemplateTransactionException("Transaction template execution failed", th);
    }

    private Throwable resolveErrorToAnalyze(final Throwable th) {
        Throwable res = th;
        if (th instanceof TemplateTransactionException) {
            // custom error (possibly thrown by nested templates) shouldn't participate in analysis
            res = th.getCause();
        }
        return res;
    }
}
